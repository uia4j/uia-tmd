package uia.tmd;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.TableType;
import uia.tmd.model.xml.TaskSpaceType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

/**
 * Task factory.
 *
 * @author Kyle K. Lin
 *
 */
public class TaskFactory {

    final TmdType tmd;

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

        this.tmd = TmdTypeHelper.load(file);
        for (ExecutorType exec : this.tmd.getExecutorSpace().getExecutor()) {
            this.executors.put(exec.getName(), exec);
        }
        for (TaskType task : this.tmd.getTaskSpace().getTask()) {
            this.tasks.put(task.getName(), task);
        }
        for (TableType table : this.tmd.getTableSpace().getTable()) {
            this.tables.put(table.getName(), table);
        }
        for (DbServerType svr : this.tmd.getDatabaseSpace().getDbServer()) {
            this.dbServers.put(svr.getId(), svr);
        }
    }

    public TmdType getTmd() {
        return this.tmd;
    }

    public Set<String> getExecutorNames() {
        return this.executors.keySet();
    }

    public TaskSpaceType getTasks() {
        return this.tmd.getTaskSpace();
    }

    public TaskType getTask(String taskName) {
        return this.tasks.get(taskName);
    }

    public void addTask(TaskType task) {
        this.tasks.put(task.getName(), task);
        this.tmd.getTaskSpace().getTask().add(task);
    }

    public DbServerType getDbServer(String dbName) {
        return this.dbServers.get(dbName);
    }

    public Map<String, TableType> getTables() {
        return this.tables;
    }

    public TableType getTable(String tableName) {
        return this.tables.get(tableName);
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

        return new SimpleTaskExecutor(this, executor);
    }
}
