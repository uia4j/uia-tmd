package uia.tmd.wip;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

import uia.tmd.TaskExecutor;
import uia.tmd.TaskExecutorListener;
import uia.tmd.TaskFactory;
import uia.tmd.Where;
import uia.tmd.WhereEq;

public class WIPTest implements TaskExecutorListener {

    private int rc;

    @Test
    public void testZTask() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZTask");
        executor.addListener(this);

        Where[] ws = new Where[1];
        ws[0] = new WhereEq("SID", "0905f3c2-4fe5-48dc-9f55-c3819e31f6ac");
        System.out.println(String.format("Execute:%s(%s)", executor.run(ws), this.rc));
    }

    @Test
    public void testShopOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ShopOrder");
        executor.addListener(this);

        Where[] ws = new Where[1];
        ws[0] = new WhereEq("HANDLE", "ShopOrderBO:1600,20150928-CFZ-001");
        // where.put("HANDLE", "ShopOrderBO:D001,PACK_SO_20150826001");
        System.out.println(String.format("Execute:%s(%s)", executor.run(ws), this.rc));
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
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
