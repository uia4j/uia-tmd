package ui.tmd.zztop;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import uia.tmd.JobRunner;
import uia.tmd.ItemRunner;
import uia.tmd.SourceSelectFilter;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.TmdUtils;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TmdTxKey;
import uia.tmd.zztop.db.dao.TmdTxKeyDao;

public class TxKeyItemRunner implements ItemRunner, SourceSelectFilter {

    private String tableName;

    private List<String> pkColumns;

    private TreeSet<String> pkValues;

    public TxKeyItemRunner() {
        this.pkValues = new TreeSet<String>();
    }

    @Override
    public String prepare(JobRunner executorRunner, ItemType itemType, TaskType taskType) throws TmdException {
        this.tableName = taskType.getSourceSelect().getTable();
        this.pkColumns = itemType.getArgs().getArg();
        try (Connection conn = DB.create()) {
            TmdTxKeyDao dao = new TmdTxKeyDao(conn);
            List<TmdTxKey> keys = dao.selectByTable(this.tableName);
            keys.stream().forEach(k -> {
                this.pkValues.add(k.getId());
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner) throws TmdException {
        try (Connection conn = DB.create()) {
            taskRunner.run(taskType, "/", null, this);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> accept(List<Map<String, Object>> sourceRows) {
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
