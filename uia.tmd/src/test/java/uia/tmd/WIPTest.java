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

        JobRunner runner = factory.createRunner("INITIAL_TEST");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    @Override
    public void sourceSelected(JobRunner jobRunner, TaskEvent evt) {
        this.rc += evt.count;
        System.out.println(evt);
    }

    @Override
    public void sourceDeleted(JobRunner jobRunner, TaskEvent evt) {
        System.out.println(evt);
    }

    @Override
    public void targetDeleted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void targetInserted(JobRunner jobRunner, TaskEvent evt) {
    }

    @Override
    public void failed(JobRunner jobRunner, TaskEvent evt, SQLException ex) {
        System.out.println(evt);
        ex.printStackTrace();
    }

    @Override
    public void done(JobRunner jobRunner) {
    }
}
