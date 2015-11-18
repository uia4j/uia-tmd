package uia.tmd.wip;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.TaskExecutor;
import uia.tmd.TaskExecutorListener;
import uia.tmd.TaskFactory;

public class WIPTest implements TaskExecutorListener {

    private int rc;

    @Test
    public void testZTask() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZTask");
        executor.addListener(this);

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("SID", "0905f3c2-4fe5-48dc-9f55-c3819e31f6ac");
        System.out.println(String.format("Execute:%s(%s)", executor.run(where), this.rc));
    }

    @Test
    public void testShopOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ShopOrder");
        executor.addListener(this);

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("HANDLE", "ShopOrderBO:1600,20150928-CFZ-001");
        // where.put("HANDLE", "ShopOrderBO:D001,PACK_SO_20150826001");
        System.out.println(String.format("Execute:%s(%s)", executor.run(where), this.rc));
    }

    @Override
    public void sourceSelected(TaskExecutorEvent evt, int count) {
        this.rc++;
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql + ", count=" + count);
        System.out.println("      " + evt.values);
    }

    @Override
    public void targetDeleted(TaskExecutorEvent evt, int count) {
        this.rc++;
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql + ", count=" + count);
        System.out.println("      " + evt.values);
    }

    @Override
    public void targetInserted(TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql);
    }

    @Override
    public void executeFailure(TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql);
        ex.printStackTrace();
    }
}
