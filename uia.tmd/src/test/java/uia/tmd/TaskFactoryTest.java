package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.TmdTypeHelperTest;

public class TaskFactoryTest implements TaskExecutorListener {

    @Test
    public void testCase1() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("CASE1");
        executor.addListener(this);

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("id", 1);
        System.out.println("Execute:" + executor.run(where));
    }

    @Test
    public void testCase2() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("CASE2");
        executor.addListener(this);

        TreeMap<String, Object> where = new TreeMap<String, Object>();
        where.put("id", 1);
        System.out.println("Execute:" + executor.run(where));
    }

    @Override
    public void sourceSelected(TaskExecutorEvent evt, int count) {
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql + ", count=" + count);
        System.out.println("    " + evt.criteria);
    }

    @Override
    public void targetDeleted(TaskExecutorEvent evt, int count) {
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql + ", count=" + count);
        System.out.println("    " + evt.criteria);
    }

    @Override
    public void targetInserted(TaskExecutorEvent evt) {
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql);
        System.out.println("    " + evt.criteria);
    }

    @Override
    public void executeFailure(TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt.jobName + "> " + evt.db + "> " + evt.sql);
        ex.printStackTrace();
    }
}
