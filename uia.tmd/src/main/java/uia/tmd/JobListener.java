package uia.tmd;

/**
 *
 * @author gazer
 *
 */
public interface JobListener {

    public void itemBegin(JobRunner executor, ExecutorEvent evt);

    public void itemEnd(JobRunner executor, ExecutorEvent evt);

    public static class ExecutorEvent {

        public final String taskName;

        public final String tableName;

        public final String criteria;

        public ExecutorEvent(String taskName, String tableName, String criteria) {
            this.taskName = taskName;
            this.tableName = tableName;
            this.criteria = criteria;
        }

        @Override
        public String toString() {
            return String.format("item> %-20s, criteria:%s",
                    this.taskName,
                    this.criteria);
        }

    }
}
