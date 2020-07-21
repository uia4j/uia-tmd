package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class MVSTest implements JobListener, TaskListener {

    @Test
    public void testIdle() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(MVSTest.class.getResource("mvs.xml").toURI()));

        JobRunner runner = factory.createRunner("MVSDB_ALL_IDLE");
        runner.addJobListener(this);
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        runner.run(null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testIvp2() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(MVSTest.class.getResource("mvs.xml").toURI()));

        JobRunner runner = factory.createRunner("MVSDB_ALL_IVP2");
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        runner.run(null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
        System.out.println(evt);
    }

    @Override
    public void taskDone(JobRunner runjobRunnerner) {
    }

    @Override
    public void jobItemBegin(JobRunner jobRunner, JobEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void jobItemEnd(JobRunner jobRunner, JobEvent evt) {
    }

    @Override
    public void jobFailed(JobRunner jobRunner, Exception ex) {
    	ex.printStackTrace();
    }

    @Override
    public void jobDone(JobRunner jobRunner) {
    }
}
