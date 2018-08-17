package uia.tmd.zztop.db;

import java.util.Date;
import java.util.UUID;

public class ExecJob {

    private String id;

    private String tmdJobBo;

    private String databaseSource;

    private String databaseTarget;

    private String tmdTaskLogBo;

    private Date executedDate;

    private Date executedTime;

    private String executedResult;

    private String runState;

    private Date deleteAfter;

    public ExecJob() {
        this.executedDate = new Date();
        this.executedTime = this.executedDate;
        this.id = this.executedDate.getTime() + "-" + UUID.randomUUID().toString();
        this.executedResult = "UNKONW";
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
