package uia.tmd.zztop;

import java.util.List;
import java.util.Map;

import uia.tmd.ItemRunner;
import uia.tmd.JobRunner;
import uia.tmd.SourceSelectFilter;
import uia.tmd.TaskRunner;
import uia.tmd.TmdException;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.TaskType;

/**
 * WOF: Without Filter
 * 
 * 1. 紀錄新同步的數據。
 *
 * @author Kyle K. Lin
 *
 */
public class TxKeyItemRunnerWOF implements ItemRunner, SourceSelectFilter {

    public TxKeyItemRunnerWOF() {
    }

    @Override
    public WhereType prepare(JobRunner jobRunner, ItemType itemType, TaskType taskType, String whereBase) throws TmdException {
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
    	return sourceRows;
    }
}
