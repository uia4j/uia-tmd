package uia.tmd;

import java.sql.SQLException;
import java.util.Map;

public interface TaskExecutorListener {

    public void sourceSelected(TaskExecutorEvent evt, int count);

    public void targetInserted(TaskExecutorEvent evt);

    public void targetDeleted(TaskExecutorEvent evt, int count);

    public void executeFailure(TaskExecutorEvent evt, SQLException ex);

    public static class TaskExecutorEvent {

        public final String db;

        public final String jobName;

        public final String sql;

        public final Map<String, Object> values;

        public TaskExecutorEvent(String db, String jobName, String sql, Map<String, Object> values) {
            this.db = db;
            this.jobName = jobName;
            this.sql = sql;
            this.values = values;
        }
    }
}
