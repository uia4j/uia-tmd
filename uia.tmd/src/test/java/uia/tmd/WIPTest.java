package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.junit.Test;

public class WIPTest implements TaskListener {

    private int rc;

    @Test
    public void testInitialTest() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        JobRunner executor = factory.createExecutor("INITIAL_TEST");
        executor.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    @Override
    public void sourceSelected(Object executor, TaskEvent evt) {
        this.rc += evt.count;
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(Object executor, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(Object executor, TaskEvent evt) {
    }

    @Override
    public void targetInserted(Object executor, TaskEvent evt) {
    }

    @Override
    public void executeFailure(Object executor, TaskEvent evt, SQLException ex) {
        System.out.println(evt);
        ex.printStackTrace();
    }

    @Override
    public void done(Object executor) {
    }
}
