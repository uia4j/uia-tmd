package uia.tmd.accessor;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;

import uia.tmd.TaskExecutor;
import uia.tmd.TaskExecutorListener;
import uia.tmd.TaskFactory;

public class WIPTest implements TaskExecutorListener {

    private int rc;

    public void testItemOperation() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZdItemOperation");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    public void testZhDcType() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZhDcType");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    public void testZdFutureActionDef() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZdFutureActionDef");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    public void testZdLookup() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZdLookup");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    public void testZLogic() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZLogic");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    public void testZNextNumber() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File(WIPTest.class.getResource("wip.xml").toURI()));

        TaskExecutor executor = factory.createExecutor("ZNextNumber");
        executor.addListener(this);

        System.out.println(String.format("Execute:%s(%s)", executor.run((String) null), this.rc));
    }

    @Override
    public void sourceSelected(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void sourceDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void targetDeleted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql + ", count=" + evt.count);
        System.out.println("      " + evt.criteria);
    }

    @Override
    public void targetInserted(TaskExecutor executor, TaskExecutorEvent evt) {
        this.rc++;
        System.out.println(evt.task.getName() + "> " + evt.sql);
    }

    @Override
    public void executeFailure(TaskExecutor executor, TaskExecutorEvent evt, SQLException ex) {
        System.out.println(evt.task.getName() + "> " + evt.sql);
        ex.printStackTrace();
    }

    @Override
    public void done(TaskExecutor executor) {
        // TODO Auto-generated method stub

    }
}
