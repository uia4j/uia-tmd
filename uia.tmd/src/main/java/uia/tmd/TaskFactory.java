package uia.tmd;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.DatabaseSpaceType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorSpaceType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.MTableType;
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

    final TreeMap<String, MTableType> tables;

    /**
     * Constructor.
     * @param file TMD XML file.
     * @throws Exception
     */
    public TaskFactory(File file) throws Exception {
        this.executors = new TreeMap<String, ExecutorType>();
        this.tasks = new TreeMap<String, TaskType>();
        this.tables = new TreeMap<String, MTableType>();
        this.dbServers = new TreeMap<String, DbServerType>();

        this.tmd = TmdTypeHelper.load(file);
        for (ExecutorType exec : this.tmd.getExecutorSpace().getExecutor()) {
            this.executors.put(exec.getName(), exec);
        }
        for (TaskType task : this.tmd.getTaskSpace().getTask()) {
            this.tasks.put(task.getName(), task);
        }
        for (MTableType table : this.tmd.getTableSpace().getTable()) {
            this.tables.put(table.getName(), table);
        }
        for (DbServerType svr : this.tmd.getDatabaseSpace().getDbServer()) {
            this.dbServers.put(svr.getId(), svr);
        }
    }

    public TmdType getTmd() {
        return this.tmd;
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

    public void removeTask(TaskType task) {
        this.tasks.remove(task.getName());
        this.tmd.getTaskSpace().getTask().remove(task);
    }

    public Set<String> getExecutorNames() {
        return this.executors.keySet();
    }

    public ExecutorSpaceType getExecutors() {
        return this.tmd.getExecutorSpace();
    }

    public void addExecutor(ExecutorType executor) {
        this.executors.put(executor.getName(), executor);
        this.tmd.getExecutorSpace().getExecutor().add(executor);
    }

    public void removeExecutor(ExecutorType executor) {
        this.executors.remove(executor.getName());
        this.tmd.getExecutorSpace().getExecutor().remove(executor);
    }

    public DatabaseSpaceType getDbServers() {
        return this.tmd.getDatabaseSpace();
    }

    public DbServerType getDbServer(String dbName) {
        return this.dbServers.get(dbName);
    }

    public void addDbServer(DbServerType dbServer) {
        this.dbServers.put(dbServer.getDbName(), dbServer);
        this.tmd.getDatabaseSpace().getDbServer().add(dbServer);
    }

    public void removeDbServer(DbServerType dbServer) {
        this.dbServers.remove(dbServer.getDbName());
        this.tmd.getDatabaseSpace().getDbServer().remove(dbServer);
    }

    public Map<String, MTableType> getTables() {
        return this.tables;
    }

    public MTableType getTable(String tableName) {
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
