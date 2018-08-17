package uia.tmd.impl;

import java.util.Arrays;
import java.util.Date;

import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;

public class MyItemRunner implements ItemRunner {

    private Date txBeginTime;

    public MyItemRunner() {
        this.txBeginTime = new Date(System.currentTimeMillis() - 3600000);
    }

    @Override
    public WhereType prepare(JobRunner executor, ItemType itemType, TaskType taskType, String whereBase) throws TmdException {
        if (itemType.getArgs().getArg().size() == 0) {
            throw new TmdException("Argument missing");
        }

        String where = whereBase == null
                ? String.format("%s>? and %s<=?",
                        itemType.getArgs().getArg().get(0),
                        itemType.getArgs().getArg().get(0))
                : String.format("%s and %s>? and %s<=?",
                        whereBase,
                        itemType.getArgs().getArg().get(0),
                        itemType.getArgs().getArg().get(0));

        return new WhereType(where, this.txBeginTime, executor.getTxTime());
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType whereType) throws TmdException {
        try {
            taskRunner.run(taskType, "/", whereType.sql, Arrays.asList(whereType.paramValues), null);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }

}
