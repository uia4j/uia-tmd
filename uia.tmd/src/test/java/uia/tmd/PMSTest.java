package uia.tmd;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class PMSTest implements TaskListener {

    private int rc;

    @Test
    public void testMasterData() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("MASTER_DATA");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    @Test
    public void testPmsSchedule() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("PMS_SCHEDULE");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    public void testPmsScheduleOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("PMS_SCHEDULE_ORDER");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    public void testRepairOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("REPAIR_ORDER");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }

    public void testPartolSheet() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("PATROL_SHEET");
        runner.addTaskListener(this);
        System.out.println(String.format("Execute:%s(%s)", runner.run((String) null), this.rc));
    }
    public void testPartolOrder() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("dbfile/pms_master.xml"));

        JobRunner runner = factory.createRunner("PATROL_ORDER");
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
    public void taskFailed(JobRunner jobRunner, TaskEvent evt, Exception ex) {
        System.out.println(evt);
        ex.printStackTrace();
    }

    @Override
    public void taskDone(JobRunner jobRunner) {
    }
}
