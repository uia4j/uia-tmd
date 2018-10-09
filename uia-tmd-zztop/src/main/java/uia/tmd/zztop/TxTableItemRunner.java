package uia.tmd.zztop;

import java.sql.Connection;
import java.util.Arrays;

import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TxTable;
import uia.tmd.zztop.db.dao.TxTableDao;

public class TxTableItemRunner implements ItemRunner {

    public TxTableItemRunner() {
    }

    @Override
    public WhereType prepare(JobRunner executor, ItemType itemType, TaskType taskType, String whereBase) throws TmdException {
        if (itemType.getArgs().getArg().size() == 0) {
            throw new TmdException("Argument missing, args(0): column name");
        }

        try (Connection conn = DB.create()) {
            TxTable txTable = new TxTableDao(conn).selectLastByTable(taskType.getSourceSelect().getTable());
            if (txTable == null) {
                return new WhereType(null)
                        .and(whereBase)
                        .and(itemType.getWhere());
            }

            // 同步 指定欄位 特定時間區間 的資料
            String where = String.format("%s>? and %s<=?",
                    itemType.getArgs().getArg().get(0),
                    itemType.getArgs().getArg().get(0));

            return new WhereType(where, txTable.getTxTime(), executor.getTxTime())
                    .and(whereBase)
                    .and(itemType.getWhere());
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType where) throws TmdException {
        try (Connection conn = DB.create()) {
            if (where.sql != null) {
                taskRunner.run(taskType, "/", where.sql, Arrays.asList(where.paramValues), null);
            }
            else {
                taskRunner.run(taskType, "/", null, null);
            }
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }

}
