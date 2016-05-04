package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.TmdTypeHelperTest;

public class TaskFactoryTest implements TaskExecutorListener {

    private int count;

    private boolean concurrent = false;

    @Test
    public void testSimple() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TreeMap<String, Object> where = new TreeMap<String, Object>();

        TaskExecutor executor = factory.createExecutor("Simple", this.concurrent);
        executor.addListener(this);

        executor.run(where);
        executor.printRunLog();
    }

    @Test
    public void testSimpleBack() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TreeMap<String, Object> where = new TreeMap<String, Object>();

        TaskExecutor executor = factory.createExecutor("SimpleBack", this.concurrent);
        executor.addListener(this);

        executor.run(where);
        executor.printRunLog();
    }

    @Test
    public void testLoop() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TreeMap<String, Object> where = new TreeMap<String, Object>();

        TaskExecutor executor = factory.createExecutor("Loop", this.concurrent);
        executor.addListener(this);

        executor.run(where);
        executor.printRunLog();
    }

    @Test
    public void testLoopBack() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TreeMap<String, Object> where = new TreeMap<String, Object>();

        TaskExecutor executor = factory.createExecutor("LoopBack", this.concurrent);
        executor.addListener(this);

        executor.run(where);
        executor.printRunLog();
    }

    @Test
    public void testCase2() throws URISyntaxException, Exception {
        this.count = 0;

        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("CASE2");
        executor.addListener(this);

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("id", 1);
        System.out.println("Execute:" + executor.run(where));
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt.task.getName() + "> source>" + evt.sql + ", count=" + evt.count);
        System.out.println("    criteria:" + evt.criteria);
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
