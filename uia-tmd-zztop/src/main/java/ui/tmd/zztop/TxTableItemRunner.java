package ui.tmd.zztop;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;

import uia.tmd.JobRunner;
import uia.tmd.ItemRunner;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.zztop.db.TmdTxTable;
import uia.tmd.zztop.db.dao.TmdTxTableDao;

public class TxTableItemRunner implements ItemRunner {

    private Date txBeginTime;

    public TxTableItemRunner() {
        this.txBeginTime = new Date();
    }

    @Override
    public String prepare(JobRunner executor, ItemType itemType, TaskType taskType) throws TmdException {
        if (itemType.getArgs().getArg().size() == 0) {
            throw new TmdException("Argument missing");
        }

        try (Connection conn = DB.create()) {
            TmdTxTable txTable = new TmdTxTableDao(conn).selectLastByTable(taskType.getSourceSelect().getTable());
            if (txTable != null) {
                this.txBeginTime = txTable.getTxTime();
            }
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }

        return String.format("%s>'%s' and %s<='%s'",
                itemType.getArgs().getArg().get(0),
                this.txBeginTime,
                itemType.getArgs().getArg().get(0),
                executor.getTxTime());
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner) throws TmdException {
        try (Connection conn = DB.create()) {
            TmdTxTable txTable = new TmdTxTableDao(conn).selectLastByTable(taskType.getSourceSelect().getTable());
            if (txTable != null) {
                String where = String.format("%s>? and %s<=?",
                        itemType.getArgs().getArg().get(0),
                        itemType.getArgs().getArg().get(0));
                taskRunner.run(taskType, "/", where, Arrays.asList(this.txBeginTime, executorRunner.getTxTime()), null);
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
