package uia.tmd.zztop;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.SourceSelectFilter;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.TmdUtils;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.conf.TmdDB;
import uia.tmd.zztop.db.dao.TxKeyDao;

/**
 * 1. 紀錄新同步的數據。
 * 2. 濾掉已同步的數據。
 *
 * @author Kyle K. Lin
 *
 */
public class TxKeyItemRunner implements ItemRunner, SourceSelectFilter {

    private String tableName;

    private List<String> pkColumns;

    private TreeSet<String> pkValues;

    public TxKeyItemRunner() {
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

        try (Connection conn = TmdDB.create()) {
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
        try {
            taskRunner.run(taskType, "/", whereType.sql, this);
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
        });
        return result;
    }
}
