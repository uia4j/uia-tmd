package uia.tmd.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import uia.tmd.JobRunner;
import uia.tmd.ItemRunner;
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
    public String prepare(JobRunner executor, ItemType itemType, TaskType taskType) throws TmdException {
        if (itemType.getArgs().getArg().size() == 0) {
            throw new TmdException("Argument missing");
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return String.format("%s>'%s' and %s<='%s'",
                itemType.getArgs().getArg().get(0),
                fmt.format(this.txBeginTime),
                itemType.getArgs().getArg().get(0),
                fmt.format(executor.getTxTime()));
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner) throws TmdException {
        try {
            String where = String.format("%s>? and %s<=?",
                    itemType.getArgs().getArg().get(0),
                    itemType.getArgs().getArg().get(0));
            taskRunner.run(taskType, "/", where, Arrays.asList(this.txBeginTime, executorRunner.getTxTime()), null);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }

}
