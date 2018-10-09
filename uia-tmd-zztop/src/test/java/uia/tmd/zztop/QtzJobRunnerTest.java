package uia.tmd.zztop;

import java.io.File;

import org.junit.Test;

import uia.tmd.TaskFactory;
import uia.tmd.zztop.QtzJobRunner;
import uia.tmd.zztop.ZztopEnv;

public class QtzJobRunnerTest {

    public QtzJobRunnerTest() throws Exception {
        ZztopEnv.initial();
    }

    @Test
    public void testPrint() throws Exception {
        new TaskFactory(new File("conf/tmd_plans.xml")).print("SHOP_ORDER");
        System.out.println();
        new TaskFactory(new File("conf/tmd_plans.xml")).print("PKG_CONTAINER");
    }

    @Test
    public void testPrintTables() throws Exception {
        new TaskFactory(new File("conf/tmd_plans.xml")).printTables("SHOP_ORDER");
        System.out.println();
        new TaskFactory(new File("conf/tmd_plans.xml")).printTables("PKG_CONTAINER");
    }

    public void testPMS_LOG() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        job.run("PMS_LOG", null);
    }

    public void testPMS_TX() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        job.run("PMS_TX", null);
    }

    public void testPMS_CONFIG() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        job.run("PMS_CONFIG", null);
    }

    @Test
    public void testSHOP_ORDER() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        long t1 = System.currentTimeMillis();
        job.run("SHOP_ORDER", null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1 + "ms");
    }

    public void testPKG_CONTAINER() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        long t1 = System.currentTimeMillis();
        job.run("PKG_CONTAINER", null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1 + "ms");
    }

    public void testSHOP_ORDER_delete() throws Exception {
        QtzJobRunner job = new QtzJobRunner();
        long t1 = System.currentTimeMillis();
        job.run("SHOP_ORDER_DELETE", null);
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1 + "ms");
    }
}
