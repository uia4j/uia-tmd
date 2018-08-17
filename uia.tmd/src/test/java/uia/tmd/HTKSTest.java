package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

public class HTKSTest implements TaskListener {

    @Test
    public void testShopOrderTestHTKS() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner runner = factory.createRunner("ShopOrderTestHTKS");
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0112'"
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'"

        runner.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testShopOrderTestPG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner runner = factory.createRunner("ShopOrderTestPG");
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0112'"
        // "HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'"

        runner.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0010'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    public void testShopOrderToPG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner runner = factory.createRunner("ShopOrder");
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        runner.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);
    }

    @Test
    public void testShopOrderDeletePG() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(HTKSTest.class.getResource("HTKS.xml").toURI()));

        JobRunner runner = factory.createRunner("ShopOrderDeletePG");
        runner.addTaskListener(this);

        long t1 = System.currentTimeMillis();
        runner.run("HANDLE='ShopOrderBO:1020,ESC-TESTSHOP-0012'");
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
    public void failed(JobRunner jobRunner, TaskEvent evt, SQLException ex) {
        System.out.println(evt);
    }

    @Override
    public void done(JobRunner jobRunner) {
    }
}
