package uia.tmd.zztop;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class OneByOneItemRunnerWOF implements ItemRunner, SourceSelectFilter {

    private static final Logger LOGGER = LogManager.getLogger(OneByOneItemRunnerWOF.class);
    
    private String tableName;

    private List<String> pkColumns;

    public OneByOneItemRunnerWOF() {
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

        return new WhereType(whereBase)
                .and(itemType.getWhere());
    }

    @Override
    public void run(JobRunner jobRunner, ItemType itemType, TaskType taskType, TaskRunner taskRunner, WhereType whereType) throws TmdException {
    	LOGGER.info("itemRunner> orig where: " + whereType.sql);
        try {
        	// 1. select all
            List<Map<String, Object>> rows = taskRunner.selectSource(taskType, "/", whereType.sql, this);
            // 2. one by one
            for(Map<String, Object> row : rows) {
            	String where = this.pkColumns.get(0) + "='" +row.get(this.pkColumns.get(0).toUpperCase()) + "'";
            	for(int k = 1; k < this.pkColumns.size(); k++) {
            		where += (" AND " + this.pkColumns.get(k) + "='" +row.get(this.pkColumns.get(k).toUpperCase()) + "'");
            	}

            	LOGGER.info("itemRunner> new  where: " + where);
                taskRunner.run(taskType, "/", where, this);
                jobRunner.commit();
            }
        }
        catch (Exception ex) {
        	ex.printStackTrace();
            throw new TmdException(ex);
        }
    }

    @Override
    public List<Map<String, Object>> accept(List<Map<String, Object>> sourceRows) {
    	return sourceRows;
    }
}
