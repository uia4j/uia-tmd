package uia.tmd;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.xml.AbstractTableType;
import uia.tmd.model.xml.DatabaseSpaceType;
import uia.tmd.model.xml.DatabaseType;
import uia.tmd.model.xml.ItemType;
import uia.tmd.model.xml.JobSpaceType;
import uia.tmd.model.xml.JobType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskSpaceType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.TmdType;

public class TaskFactory {

    final TmdType tmd;

    final TreeMap<String, JobType> jobs;

    final TreeMap<String, TaskType> tasks;

    final TreeMap<String, AbstractTableType> tables;

    final TreeMap<String, DatabaseType> databases;

    public TaskFactory(File file) throws Exception {
        this.jobs = new TreeMap<String, JobType>();
        this.tasks = new TreeMap<String, TaskType>();
        this.tables = new TreeMap<String, AbstractTableType>();
        this.databases = new TreeMap<String, DatabaseType>();

        this.tmd = TmdTypeHelper.load(file);
        for (JobType job : this.tmd.getJobSpace().getJob()) {
            this.jobs.put(job.getName(), job);
        }
        for (TaskType task : this.tmd.getTaskSpace().getTask()) {
            this.tasks.put(task.getName(), task);
        }
        for (AbstractTableType table : this.tmd.getTableSpace().getTable()) {
            this.tables.put(table.getName(), table);
        }
        for (DatabaseType database : this.tmd.getDatabaseSpace().getDatabase()) {
            this.databases.put(database.getId(), database);
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

    public Set<String> getJobNames() {
        return this.jobs.keySet();
    }

    public JobSpaceType getJobs() {
        return this.tmd.getJobSpace();
    }

    public void addJob(JobType jobType) {
        this.jobs.put(jobType.getName(), jobType);
        this.tmd.getJobSpace().getJob().add(jobType);
    }

    public void removeJob(JobType jobType) {
        this.jobs.remove(jobType.getName());
        this.tmd.getJobSpace().getJob().remove(jobType);
    }

    public DatabaseType getDatabase(String database) {
        return this.databases.get(database);
    }

    public DatabaseSpaceType getDatabases() {
        return this.tmd.getDatabaseSpace();
    }

    public void addDatabase(DatabaseType databaese) {
        this.databases.put(databaese.getDbName(), databaese);
        this.tmd.getDatabaseSpace().getDatabase().add(databaese);
    }

    public void removeDatabase(DatabaseType databaese) {
        this.databases.remove(databaese.getDbName());
        this.tmd.getDatabaseSpace().getDatabase().remove(databaese);
    }

    public Map<String, AbstractTableType> getTables() {
        return this.tables;
    }

    public AbstractTableType getTable(String tableName) {
        return this.tables.get(tableName);
    }

    public JobRunner createRunner(String jobName) throws Exception {
        JobType jobType = this.jobs.get(jobName);
        if (jobType == null) {
            return null;
        }

        return new JobRunner(this, jobType);
    }

    public void print(String jobName) {
        JobType jobType = this.jobs.get(jobName);
        for (ItemType itemType : jobType.getItem()) {
            print(itemType.getTaskName(), 0);
        }
    }

    private void print(String taskName, int deep) {
        int b = Math.max(0, deep - 3);
        for (int i = 0; i < b; i++) {
            System.out.print(" ");
        }
        if (deep > 0) {
            System.out.print("┖");
        }
        for (int i = b + 1; i < deep; i++) {
            System.out.print("─");
        }
        System.out.println(taskName);
        TaskType taskType = getTask(taskName);
        if (taskType.getNext() != null && taskType.getNext().getPlan() != null) {
            for (PlanType planType : taskType.getNext().getPlan()) {
                print(planType.getTaskName(), deep + 3);
            }
        }
    }
}
