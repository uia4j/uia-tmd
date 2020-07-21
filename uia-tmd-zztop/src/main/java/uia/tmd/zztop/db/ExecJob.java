package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

import uia.dao.annotation.ColumnInfo;
import uia.dao.annotation.TableInfo;

@TableInfo(name = "zzt_exec_job", orderBy = "executed_date desc")
public class ExecJob {
	
	public static final String KEY = "zzt_exec_job";

	@ColumnInfo(name = "id", primaryKey = true)
    private String id;

	@ColumnInfo(name = "tmd_job_bo")
    private String tmdJobBo;

	@ColumnInfo(name = "database_source")
    private String databaseSource;

	@ColumnInfo(name = "database_target")
    private String databaseTarget;

	@ColumnInfo(name = "tmd_task_log_bo")
    private String tmdTaskLogBo;

	@ColumnInfo(name = "executed_date")
    private Date executedDate;

	@ColumnInfo(name = "executed_time")
    private Date executedTime;

	@ColumnInfo(name = "executed_result")
    private String executedResult;

	@ColumnInfo(name = "run_state")
    private String runState;

	@ColumnInfo(name = "delete_after")
    private Date deleteAfter;

    public ExecJob() {
        this.executedDate = new Date();
        this.executedTime = this.executedDate;
        this.id = this.executedDate.getTime() + "-" + UUID.randomUUID().toString();
        this.executedResult = "UNKNOWN";
        this.runState = "RUN";
    }

    public ExecJob(ExecJob data) {
        this.id = data.id;
        this.tmdJobBo = data.tmdJobBo;
        this.databaseSource = data.databaseSource;
        this.databaseTarget = data.databaseTarget;
        this.tmdTaskLogBo = data.tmdTaskLogBo;
        this.executedDate = data.executedDate;
        this.executedTime = data.executedTime;
        this.executedResult = data.executedResult;
        this.runState = data.runState;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTmdJobBo() {
        return this.tmdJobBo;
    }

    public void setTmdJobBo(String tmdJobBo) {
        this.tmdJobBo = tmdJobBo;
    }

    public String getDatabaseSource() {
        return this.databaseSource;
    }

    public void setDatabaseSource(String databaseSource) {
        this.databaseSource = databaseSource;
    }

    public String getDatabaseTarget() {
        return this.databaseTarget;
    }

    public void setDatabaseTarget(String databaseTarget) {
        this.databaseTarget = databaseTarget;
    }

    public String getTmdTaskLogBo() {
        return this.tmdTaskLogBo;
    }

    public void setTmdTaskLogBo(String tmdTaskLogBo) {
        this.tmdTaskLogBo = tmdTaskLogBo;
    }

    public Date getExecutedDate() {
        return this.executedDate;
    }

    public void setExecutedDate(Date executedDate) {
        this.executedDate = executedDate;
    }

    public Date getExecutedTime() {
        return this.executedTime;
    }

    public void setExecutedTime(Date executedTime) {
        this.executedTime = executedTime;
    }

    public String getExecutedResult() {
        return this.executedResult;
    }

    public void setExecutedResult(String executedResult) {
        this.executedResult = executedResult;
    }

    public String getRunState() {
        return this.runState;
    }

    public void setRunState(String runState) {
        this.runState = runState;
    }

    public Date getDeleteAfter() {
        return this.deleteAfter;
    }

    public void setDeleteAfter(Date deleteAfter) {
        this.deleteAfter = deleteAfter;
    }

    @Override
    public ExecJob clone() {
        return new ExecJob(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
