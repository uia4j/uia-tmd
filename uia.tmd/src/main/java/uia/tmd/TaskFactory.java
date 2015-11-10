package uia.tmd;

import java.io.File;
import java.util.TreeMap;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

/**
 * Task factory.
 *
 * @author Kyle K. Lin
 *
 */
public class TaskFactory {

    private TreeMap<String, TaskType> tasks;

    private TreeMap<String, DbServerType> dbServers;

    /**
     * Constructor.
     * @param file TMD XML file.
     * @throws Exception
     */
    public TaskFactory(File file) throws Exception {
        this.tasks = new TreeMap<String, TaskType>();
        this.dbServers = new TreeMap<String, DbServerType>();

        TmdType tmd = TmdTypeHelper.load(file);
        tmd.getTaskSpace().getTask().stream().forEach(t -> this.tasks.put(t.getName(), t));
        tmd.getDatabaseSpace().getDbServer().stream().forEach(s -> this.dbServers.put(s.getId(), s));
    }

    /**
     * Create task executor.
     * @param taskName Task name defined in TMD XML file.
     * @return Executor.
     * @throws Exception
     */
    public TaskExecutor createExecutor(String taskName) throws Exception {
        TaskType task = this.tasks.get(taskName);
        if (task == null) {
            return null;
        }

        return new TaskExecutor(task, this.dbServers.get(task.getSource()), this.dbServers.get(task.getTarget()));
    }
}
