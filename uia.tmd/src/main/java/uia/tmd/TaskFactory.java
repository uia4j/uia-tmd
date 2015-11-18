package uia.tmd;

import java.io.File;
import java.util.TreeMap;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.TableType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

/**
 * Task factory.
 *
 * @author Kyle K. Lin
 *
 */
public class TaskFactory {

    final TreeMap<String, ExecutorType> executors;

    final TreeMap<String, TaskType> tasks;

    final TreeMap<String, DbServerType> dbServers;

    final TreeMap<String, TableType> tables;

    /**
     * Constructor.
     * @param file TMD XML file.
     * @throws Exception
     */
    public TaskFactory(File file) throws Exception {
        this.executors = new TreeMap<String, ExecutorType>();
        this.tasks = new TreeMap<String, TaskType>();
        this.tables = new TreeMap<String, TableType>();
        this.dbServers = new TreeMap<String, DbServerType>();

        TmdType tmd = TmdTypeHelper.load(file);
        for (ExecutorType exec : tmd.getExecutorSpace().getExecutor()) {
            this.executors.put(exec.getName(), exec);
        }
        for (TaskType task : tmd.getTaskSpace().getTask()) {
            this.tasks.put(task.getName(), task);
        }
        for (TableType table : tmd.getTableSpace().getTable()) {
            this.tables.put(table.getName(), table);
        }
        for (DbServerType svr : tmd.getDatabaseSpace().getDbServer()) {
            this.dbServers.put(svr.getId(), svr);
        }
    }

    /**
     * Create task executor.
     * @param execName Task name defined in TMD XML file.
     * @return Executor.
     * @throws Exception
     */
    public TaskExecutor createExecutor(String execName) throws Exception {
        ExecutorType executor = this.executors.get(execName);
        if (executor == null) {
            return null;
        }

        return new TaskExecutor(this, executor);
    }
}
