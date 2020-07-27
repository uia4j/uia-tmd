package uia.tmd;

import java.util.Arrays;

import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;

public class ItemRunnerImpl implements ItemRunner {

    @Override
    public WhereType prepare(JobRunner jobRunner, ItemType itemType, TaskType taskType, String whereBase) throws TmdException {
        if (itemType.getArgs() == null || itemType.getArgs().getArg().isEmpty()) {
            return new WhereType(whereBase)
                    .and(itemType.getWhere());
        }

        return new WhereType(itemType.getArgs().getArg().get(0))
                .and(whereBase)
                .and(itemType.getWhere());
    }

    @Override
    public void run(JobRunner jobRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType where) throws TmdException {
        try {
            taskRunner.run(taskType, "/", where.sql, Arrays.asList(where.paramValues), null);
        }
        catch (Exception ex) {
            throw new TmdException(ex);
        }
    }
}
