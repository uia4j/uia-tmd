package uia.tmd;

/**
 *
 * @author gazer
 *
 */
public interface JobListener {

    public void jobItemBegin(JobRunner jobRunner, JobEvent evt) throws Exception;

    public void jobItemEnd(JobRunner jobRunner, JobEvent evt) throws Exception;

    public void jobFailed(JobRunner jobRunner, Exception ex) throws Exception;

    public void jobDone(JobRunner jobRunner) throws Exception;

    public static class JobEvent {

        public final String taskName;

        public final String tableName;

        public final String criteria;

        public JobEvent(String taskName, String tableName, String criteria) {
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
