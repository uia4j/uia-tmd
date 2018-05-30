package uia.tmd.wip;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

import uia.tmd.TaskExecutor;
import uia.tmd.TaskExecutorListener;
import uia.tmd.TaskFactory;

public class HTKSTest implements TaskExecutorListener {

    private int rc;

    @Test
    public void testZTask() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("htks.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZTask");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    @Test
    public void testShopOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ShopOrder");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql);
    }

    @Override
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt.task.getName() + "> " + evt.sql);
        ex.printStackTrace();
    }

    @Override
    public void done(TaskExecutor executor) {
        // TODO Auto-generated method stub

    }
}
