package uia.tmd;

import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

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
     * @param executor Executor.
     * @param evt
     */
    public void done(TaskExecutor task);

    /**
     *
     * @author gazer
     *
     */
    public static class TaskExecutorEvent {

        public enum Database {
            SOURCE,
            TARGET
        }

        public final TaskType task;

        public final String parentPath;

        public final String path;

        public final String sql;

        public final Map<String, Object> criteria;

        public final int count;

        public final Database database;

        public TaskExecutorEvent(TaskType task, String parentPath, String sql, Where[] wheres, int count, Database database) {
            this.task = task;
            this.parentPath = parentPath;
            this.path = parentPath + task.getName() + "/";
            this.sql = sql;
            this.criteria = new TreeMap<String, Object>();
            this.database = database;
            for (Where where : wheres) {
                this.criteria.put(where.getParamName(), where.getParamValue());
            }
            this.count = count;
        }

        public TaskExecutorEvent(TaskType task, String parentPath, String sql, Map<String, Object> criteriaValues, int count, Database database) {
            this.task = task;
            this.parentPath = parentPath;
            this.path = parentPath + this.task.getName() + "/";
            this.sql = sql;
            this.criteria = criteriaValues;
            this.count = count;
            this.database = database;
        }
    }
}
