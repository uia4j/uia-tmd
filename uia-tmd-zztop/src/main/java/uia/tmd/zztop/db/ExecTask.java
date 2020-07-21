package uia.tmd.zztop.db;

import java.util.UUID;

import uia.dao.annotation.ColumnInfo;
import uia.dao.annotation.TableInfo;

@TableInfo(name = "zzt_exec_task")
public class ExecTask {
	
	public static final String KEY = "zzt_exec_task";

	@ColumnInfo(name = "id", primaryKey = true)
    private String id;

	@ColumnInfo(name = "tmd_task_bo")
    private String tmdTaskBo;

	@ColumnInfo(name = "exec_job_bo")
    private String execJobBo;

	@ColumnInfo(name = "table_name")
    private String tableName;

	@ColumnInfo(name = "sql_where")
    private String sqlWhere;

	@ColumnInfo(name = "triggered_by")
    private String triggeredBy;

	@ColumnInfo(name = "result_count")
    private int resultCount;

	@ColumnInfo(name = "task_path")
    private String taskPath;

	@ColumnInfo(name = "group_id")
    private String groupId;

    public ExecTask() {
        this.id = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    }

    public ExecTask(ExecTask data) {
        this.id = data.id;
        this.tmdTaskBo = data.tmdTaskBo;
        this.execJobBo = data.execJobBo;
        this.tableName = data.tableName;
        this.sqlWhere = data.sqlWhere;
        this.triggeredBy = data.triggeredBy;
        this.resultCount = data.resultCount;
        this.taskPath = data.taskPath;
        this.groupId = data.groupId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTmdTaskBo() {
        return this.tmdTaskBo;
    }

    public void setTmdTaskBo(String tmdTaskBo) {
        this.tmdTaskBo = tmdTaskBo;
    }

    public String getExecJobBo() {
        return this.execJobBo;
    }

    public void setExecJobBo(String execJobBo) {
        this.execJobBo = execJobBo;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSqlWhere() {
        return this.sqlWhere;
    }

    public void setSqlWhere(String sqlWhere) {
        this.sqlWhere = sqlWhere;
    }

    public String getTriggeredBy() {
        return this.triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public int getResultCount() {
        return this.resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public String getTaskPath() {
        return this.taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
    public ExecTask clone() {
        return new ExecTask(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
