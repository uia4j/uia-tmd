package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

public class IVPTest implements JobListener, TaskListener {

    @Test
    public void testIdle() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(IVPTest.class.getResource("IVP.xml").toURI()));

        JobRunner executor = factory.createExecutor("MVSDB_ALL_IDLE");
        executor.addExecutorListener(this);
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        executor.run(null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testIvp2() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(IVPTest.class.getResource("IVP.xml").toURI()));

        JobRunner executor = factory.createExecutor("MVSDB_ALL_IVP2");
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        executor.run(null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Override
    public void sourceSelected(Object executor, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(Object executor, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(Object executor, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetInserted(Object executor, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void executeFailure(Object executor, TaskEvent evt, SQLException ex) {
        System.out.println(evt);
    }

    @Override
    public void done(Object executor) {
    }

    @Override
    public void itemBegin(JobRunner executor, ExecutorEvent evt) {
        System.out.println(evt);

    }

    @Override
    public void itemEnd(JobRunner executor, ExecutorEvent evt) {
    }
}
