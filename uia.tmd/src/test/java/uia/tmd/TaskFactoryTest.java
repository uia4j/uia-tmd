package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.TmdTypeHelperTest;

public class TaskFactoryTest {

    @Test
    public void testHuman() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("human");

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("id", "21");
        executor.run(where);
    }

    @Test
    public void testHuman2pg() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("human2pg");

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("id", "21");
        executor.run(where);
    }
}
