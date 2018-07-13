package uia.tmd;

import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;

public class ItemRunnerImpl implements ItemRunner {

    @Override
    public String prepare(JobRunner executorRunner, ItemType itemType, TaskType taskType) throws TmdException {
        return "";
    }

    @Override
    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner) throws TmdException {
        try {
            taskRunner.run(taskType, "/", null, null);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }
}
