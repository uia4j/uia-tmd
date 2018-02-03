package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

import uia.tmd.model.TmdTypeHelperTest;

public class TaskFactoryTest implements TaskExecutorListener {

    private int count;

    @Test
    public void testSimple() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("Simple");
        executor.addListener(this);

        long t1 = System.currentTimeMillis();
        executor.run(new Where[0]);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testSimpleBack() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("SimpleBack");
        executor.addListener(this);

        long t1 = System.currentTimeMillis();
        executor.run(new Where[0]);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testLoop() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("Loop");
        executor.addListener(this);

        executor.run(new Where[0]);
        executor.printRunLog();
    }

    @Test
    public void testLoopBack() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("LoopBack");
        executor.addListener(this);

        executor.run(new Where[0]);
        executor.printRunLog();
    }

    @Test
    public void testCase2() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("CASE2");
        executor.addListener(this);

        Where[] ws = new Where[1];
        ws[0] = new WhereEq("id", 1);
        System.out.println("Execute:" + executor.run(ws));
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt.task.getName() + "> source>" + evt.sql + ", count=" + evt.count);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        //System.out.println(evt.task.getName() + "> source>" + evt.sql + ", count=" + evt.count);
        //System.out.println("    criteria:" + evt.criteria);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        //System.out.println(evt.task.getName() + "> target> " + evt.sql + ", count=" + evt.count);
        //System.out.println("    criteria:" + evt.criteria);
    }

    @Override
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.count += evt.count;
        //System.out.println(evt.task.getName() + "> target>" + evt.sql);
        //System.out.println("    criteria:" + evt.criteria);
    }

    @Override
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt.task.getName() + "> " + evt.sql);
        ex.printStackTrace();
    }

    @Override
    public void done(TaskExecutor executor) {
        System.out.println("total: " + this.count);

    }
}
