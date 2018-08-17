package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gazer
 *
 */
public interface TaskListener {

    /**
     * Source to be selected.
     * @param job job.
     * @param evt Event.
     */
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt);

    /**
     * Source to be selected.
     * @param job job.
     * @param evt Event.
     */
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt);

    /**
     * Target to be inserted.
     * @param job job.
     * @param evt Event.
     */
    public void targetInserted(JobRunner jobRunner, TaskEvent evt);

    /**
     * Target to be deleted.
     * @param job job.
     * @param evt Event.
     */
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt);

    /**
     * Failed to be executed.
     * @param job job.
     * @param evt Event.
     * @param ex Exception.
     */
    public void failed(JobRunner jobRunner, TaskEvent evt, SQLException ex);

    /**
     *
     * @param task
     */
    public void done(JobRunner jobRunner);

    /**
     *
     * @author gazer
     *
     */
    public static class TaskEvent {

        public final String taskName;

        public final String parentPath;

        public final String owner;

        public final String tableName;

        public final String sql;

        public final int count;

        public final List<String> keys;

        public TaskEvent(String taskName, String parentPath, String owner, String tableName, String sql, int count) {
            this.taskName = taskName;
            this.parentPath = parentPath;
            this.owner = owner;
            this.tableName = tableName;
            this.sql = sql;
            this.count = count;
            this.keys = new ArrayList<String>();
        }

        public TaskEvent(String taskName, String parentPath, String owner, String tableName, String sql, int count, List<String> keys) {
            this.taskName = taskName;
            this.parentPath = parentPath;
            this.owner = owner;
            this.tableName = tableName;
            this.sql = sql;
            this.count = count;
            this.keys = keys;
        }

        public String getPath() {
            return this.parentPath + "/" + this.taskName;
        }

        @Override
        public String toString() {
            return String.format("%-70s> %s, %4s, %s: %s",
                    this.parentPath + "/" + this.taskName,
                    this.owner,
                    this.count,
                    this.tableName.toUpperCase(),
                    this.sql);
        }
    }
}
