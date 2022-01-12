package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class SampleTest implements TaskListener {

    private int rc;

    @Test
    public void testCase1() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/Sample.xml"));

        JobRunner runner = factory.createRunner("Case1");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        this.rc += evt.count;
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
        System.out.println(evt);
        ex.printStackTrace();
    }

    @Override
    public void taskDone(JobRunner jobRunner) {
    }
}
