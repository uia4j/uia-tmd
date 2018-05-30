package uia.tmd;

import java.sql.SQLException;

import uia.tmd.model.xml.TaskType;

/**
 *
 * @author gazer
 *
 */
public interface TaskExecutorListener {

    /**
     * Source to be selected.
     * @param executor Executor.
     * @param evt Event.
     */
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt);

    /**
     * Source to be selected.
     * @param executor Executor.
     * @param evt Event.
     */
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt);

    /**
     * Target to be inserted.
     * @param executor Executor.
     * @param evt Event.
     */
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt);

    /**
     * Target to be deleted.
     * @param executor Executor.
     * @param evt Event.
     */
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt);

    /**
     * Failed to be executed.
     * @param executor Executor.
     * @param evt Event.
     * @param ex Exception.
     */
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex);

    /**
     *
     * @param task
     */
    public void done(TaskExecutor task);

    /**
     *
     * @author gazer
     *
     */
    public static class TaskExecutorEvent {

        public enum DatabaseType {
            SOURCE,
            TARGET
        }

        public final TaskType task;

        public final String parentPath;

        public final String path;

        public final String sql;

        public final String criteria;

        public final int count;

        public final DatabaseType database;

        public TaskExecutorEvent(TaskType task, String parentPath, String sql, String where, int count, DatabaseType database) {
            this.task = task;
            this.parentPath = parentPath;
            this.path = parentPath + task.getName() + "/";
            this.sql = sql;
            this.criteria = where;
            this.database = database;
            this.count = count;
        }

        @Override
        public String toString() {
            return String.format("%s> %s\n%s> %s, %3s, %s",
                    this.database,
                    this.parentPath + this.task.getName(),
                    this.database,
                    this.sql,
                    this.count,
                    this.criteria);
        }
    }
}
