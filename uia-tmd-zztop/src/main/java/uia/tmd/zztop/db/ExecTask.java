package uia.tmd.zztop.db;

import java.util.UUID;

public class ExecTask {

    private String id;

    private String tmdTaskBo;

    private String execJobBo;

    private String tableName;

    private String sqlWhere;

    private String triggeredBy;

    private int resultCount;

    private String taskPath;

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

    @Override
    public ExecTask clone() {
        return new ExecTask(this);
    }

    @Override
    public String toString() {
        return this.id;
    }
}
