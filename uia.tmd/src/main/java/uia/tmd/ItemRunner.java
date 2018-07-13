package uia.tmd;

import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;

public interface ItemRunner {

    public String prepare(JobRunner executorRunner, ItemType itemType, TaskType taskType) throws TmdException;

    public void run(JobRunner executorRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner) throws TmdException;
}
