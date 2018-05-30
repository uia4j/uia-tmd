package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

public class TaskFactoryTest implements TaskExecutorListener {

    private int count;

    @Test
    public void testSimple() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TaskFactoryTest.class.getResource("WIP.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("Case1");
        executor.addListener(this);

        long t1 = System.currentTimeMillis();
        executor.run((String) null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt);
    }

    @Override
    public void done(TaskExecutor executor) {
        System.out.println("total: " + this.count);

    }
}
