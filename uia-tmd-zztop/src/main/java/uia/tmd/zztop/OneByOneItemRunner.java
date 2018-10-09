package uia.tmd.zztop;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.SourceSelectFilter;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.TmdUtils;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.dao.TxKeyDao;

/**
 * 1. 紀錄新同步的數據。
 * 2. 濾掉已同步的數據。
 *
 * @author Kyle K. Lin
 *
 */
public class OneByOneItemRunner implements ItemRunner, SourceSelectFilter {

    private static final Logger LOGGER = LogManager.getLogger(OneByOneItemRunner.class);
    
    private String tableName;

    private List<String> pkColumns;

    private TreeSet<String> pkValues;

    public OneByOneItemRunner() {
        this.pkValues = new TreeSet<String>();
    }

    @Override
    public WhereType prepare(JobRunner jobRunner, ItemType itemType, TaskType taskType, String whereBase) throws TmdException {
        this.tableName = taskType.getSourceSelect().getTable();
        try {
            this.pkColumns = jobRunner.pkInSource(this.tableName);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }

        try (Connection conn = DB.create()) {
            TxKeyDao dao = new TxKeyDao(conn);
            List<TxKey> keys = dao.selectByTable(this.tableName);
            keys.stream().forEach(k -> {
                this.pkValues.add(k.getId());
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return new WhereType(whereBase)
                .and(itemType.getWhere());
    }

    @Override
    public void run(JobRunner jobRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType whereType) throws TmdException {
    	LOGGER.info("itemRunner> orig: " + whereType.sql);
        try {
            List<Map<String, Object>> rows = taskRunner.selectSource(taskType, "/", whereType.sql, this);
            for(Map<String, Object> row : rows) {
            	String where = this.pkColumns.get(0) + "='" +row.get(this.pkColumns.get(0)) + "'";
            	for(int k = 1; k < this.pkColumns.size(); k++) {
            		where += (" AND " + this.pkColumns.get(k) + "='" +row.get(this.pkColumns.get(k)) + "'");
            	}

            	LOGGER.info("itemRunner> new: " + whereType.sql);
                taskRunner.run(taskType, "/", where, this);
                jobRunner.commit();
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new TmdException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> accept(List<Map<String, Object>> sourceRows) {
        // 濾掉 已同步 的數據
        ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        sourceRows.forEach(r -> {
            String pk = TmdUtils.generateKey(this.tableName, this.pkColumns, r);
            // check
            if (!this.pkValues.contains(pk)) {
                result.add(r);
            }
        });
        return result;
    }
}
