package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

public class HTKSTest implements TaskListener {

    @Test
    public void testShopOrderTestHTKS() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner executor = factory.createExecutor("ShopOrderTestHTKS");
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0112'"
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'"

        executor.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testShopOrderTestPG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner executor = factory.createExecutor("ShopOrderTestPG");
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0112'"
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'"

        executor.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    public void testShopOrderToPG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner executor = factory.createExecutor("ShopOrder");
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        executor.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testShopOrderDeletePG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner executor = factory.createExecutor("ShopOrderDeletePG");
        executor.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        executor.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
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
}
