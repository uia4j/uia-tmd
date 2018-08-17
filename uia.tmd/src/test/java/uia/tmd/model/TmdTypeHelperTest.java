package uia.tmd.model;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import uia.tmd.model.xml.JobType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class TmdTypeHelperTest {

    @Test
    public void testSample() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("Sample.xml").toURI()));

        Assert.assertEquals(3, tmd.getJobSpace().getJob().size());
        JobType job1 = tmd.getJobSpace().getJob().get(0);   // '>'
        Assert.assertEquals(
                "SITE='1020' and ACTUAL_COMP_DATE is not null and now()>add_days(ACTUAL_COMP_DATE, 90)",
                job1.getItem().get(0).getWhere());
        JobType job2 = tmd.getJobSpace().getJob().get(1);   // translate '&lt;' to '<'
        Assert.assertEquals(
                "SITE='1020' and ACTUAL_COMP_DATE is not null and now()<add_days(ACTUAL_COMP_DATE, 90)",
                job2.getItem().get(0).getWhere());
        JobType job3 = tmd.getJobSpace().getJob().get(2);
        Assert.assertEquals(3, job3.getItem().size());

        Assert.assertEquals(4, tmd.getTaskSpace().getTask().size());
        // Task1
        TaskType task1 = tmd.getTaskSpace().getTask().get(0);
        Assert.assertEquals("TABLE1", task1.getSourceSelect().getTable());
        Assert.assertNotNull(task1.getTargetUpdate());
        Assert.assertNull(task1.getNext());

        // Task2
        TaskType task2 = tmd.getTaskSpace().getTask().get(1);
        Assert.assertEquals("SOURCE_TABLE2", task2.getSourceSelect().getTable());
        Assert.assertEquals("TARGET_TABLE2", task2.getTargetUpdate().getTable());
        Assert.assertEquals(3, task2.getTargetUpdate().getColumnMapping().getColumn().size());
        Assert.assertNull(task2.getNext());

        // Task3
        TaskType task3 = tmd.getTaskSpace().getTask().get(2);
        Assert.assertEquals("TABLE3", task3.getSourceSelect().getTable());
        Assert.assertNull(task3.getTargetUpdate().getColumnMapping());
        Assert.assertEquals(2, task3.getNext().getPlan().size());
        Assert.assertEquals("A=?", task3.getNext().getPlan().get(0).getWhere());
        Assert.assertNotNull(task3.getNext().getPlan().get(1).getWhere());
        Assert.assertEquals(3, tmd.getDatabaseSpace().getDatabase().size());
        Assert.assertEquals(1, tmd.getTableSpace().getTable().get(0).getPk().getColumn().size());
        Assert.assertEquals(2, tmd.getTableSpace().getTable().get(1).getPk().getColumn().size());
        Assert.assertEquals(2, tmd.getTableSpace().getTable().size());

        // Task4
        TaskType task4 = tmd.getTaskSpace().getTask().get(3);
        PlanType plan4 = task4.getNext().getPlan().get(0);
        Assert.assertEquals("A=? and B=? and C like ? and D ? between ?", plan4.getWhere());
        Assert.assertEquals(5, plan4.getParam().size());
        Assert.assertNotNull(plan4.getParam().get(0).getSourceColumn());
        Assert.assertNull(plan4.getParam().get(0).getText());
        Assert.assertNull(plan4.getParam().get(0).getPrefix());
        Assert.assertNull(plan4.getParam().get(0).getPostfix());

    }
}
