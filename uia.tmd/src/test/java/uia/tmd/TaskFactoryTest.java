package uia.tmd;

import java.io.File;

import org.junit.Test;

import uia.tmd.TaskFactory;

public class TaskFactoryTest {

    @Test
    public void testPrint() throws Exception {
        new TaskFactory(new File(MVSTest.class.getResource("htks.xml").toURI())).print("SO_DAILY_SYNC");
    }

    @Test
    public void testPrintTables() throws Exception {
        new TaskFactory(new File(MVSTest.class.getResource("htks.xml").toURI())).printTables("SO_DAILY_SYNC");
        System.out.println();
    }
}
