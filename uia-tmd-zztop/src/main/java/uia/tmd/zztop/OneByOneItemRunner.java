package uia.tmd.zztop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.dao.DaoSession;
import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.SourceSelectFilter;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.TmdUtils;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.conf.ZZTOP;
import uia.tmd.zztop.db.dao.TxKeyDao;

/**
 * 以 Row 為 commit 目標。
 * 
 * 1. 紀錄新同步的數據。
 * 2. 濾掉已同步的數據。
 *
 * @author Kyle K. Lin
 *
 */
public class OneByOneItemRunner implements ItemRunner, SourceSelectFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneByOneItemRunner.class);
    
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

        try (DaoSession session = ZZTOP.env().createSession()) {
            TxKeyDao dao = session.tableDao(TxKeyDao.class);
            List<TxKey> keys = dao.selectByTable(this.tableName);
            keys.stream().forEach(k -> {
                this.pkValues.add(k.getId());
            });
        }
        catch (Exception ex) {
        	LOGGER.error("prepare> ", ex);
        }

        return new WhereType(whereBase)
                .and(itemType.getWhere());
    }

    @Override
    public void run(final JobRunner jobRunner, final ItemType itemType, final TaskType taskType, final TaskRunner taskRunner, final WhereType whereType) throws TmdException {
        try {
        	// 1. select rows to migrate, but do nothing
            List<Map<String, Object>> rows = taskRunner.selectSource(taskType, "/", whereType.sql, this);
        	LOGGER.info("itemRunner> " + taskType.getName() + ", count=" + rows.size());
        	
        	// 2. one by one
            for(final Map<String, Object> row : rows) {
            	// 
    	    	String pk = "" + row.get(this.pkColumns.get(0).toUpperCase());
            	String where = this.pkColumns.get(0) + "='" + row.get(this.pkColumns.get(0).toUpperCase()) + "'";
            	for(int k = 1; k < this.pkColumns.size(); k++) {
            		where += (" AND " + this.pkColumns.get(k) + "='" + row.get(this.pkColumns.get(k).toUpperCase()) + "'");
            	}

            	// re-select by PK
            	LOGGER.info("itemRunner> " + taskType.getName() + ", " + where);
            	jobRunner.setTxId(pk);
                taskRunner.run(taskType, "/", where, this);
                jobRunner.commit();
            }
        }
        catch (Exception ex) {
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
            else {
            	LOGGER.info("itemRunner> already executed. ignore: pk:" + pk);
            }
        });
        return result;
    }
    
    public static class ExecuteResult {
    	
    	public final boolean ok;
    	
    	public final String rowKey;
    	
    	public final String message;

    	public ExecuteResult() {
    		this.ok = true;
    		this.rowKey = null;
    		this.message = null;
    	}
    	
    	public ExecuteResult(boolean ok, String rowKey, String message) {
    		this.ok = ok;
    		this.rowKey = rowKey;
    		this.message = message;
    	}
    	
    }
}
