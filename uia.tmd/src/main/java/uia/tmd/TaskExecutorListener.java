package uia.tmd;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

public interface TaskExecutorListener {

    public void sourceSelected(TaskExecutorEvent evt, int count);

    public void targetInserted(TaskExecutorEvent evt);

    public void targetDeleted(TaskExecutorEvent evt, int count);

    public void executeFailure(TaskExecutorEvent evt, SQLException ex);

    public static class TaskExecutorEvent {

        public final String db;

        public final String jobName;

        public final String sql;

        public final Map<String, Object> criteria;

        public TaskExecutorEvent(String db, String jobName, String sql, Where[] wheres) {
            this.db = db;
            this.jobName = jobName;
            this.sql = sql;
            this.criteria = new TreeMap<String, Object>();
            for (Where where : wheres) {
                this.criteria.put(where.getParamName(), where.getParamValue());
            }
        }

        public TaskExecutorEvent(String db, String jobName, String sql, Map<String, Object> criteriaValues) {
            this.db = db;
            this.jobName = jobName;
            this.sql = sql;
            this.criteria = criteriaValues;
        }
    }
}
