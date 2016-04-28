package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.Database;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.CriteriaType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public class TaskExecutor {

    private final ArrayList<TaskExecutorListener> listeners;

    private final TaskFactory factory;

    private final TaskType task1;

    private final DbServerType source;

    private final DbServerType target;

    private final DataAccessor sourceAccessor;

    private final DataAccessor targetAccessor;

    private boolean deleteTarget;

    private TreeMap<String, List<String>> tableRows;

    private String name;

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param factory Task factory.
     * @throws Exception
     */
    TaskExecutor(TaskFactory factory, ExecutorType executor) throws Exception {
        this.factory = factory;
        this.name = executor.getName();
        this.task1 = this.factory.tasks.get(executor.getTask());
        this.source = this.factory.dbServers.get(executor.getSource());
        this.target = this.factory.dbServers.get(executor.getTarget());
        this.sourceAccessor = AbstractDataAccessor.create(this.source, factory.tables);
        this.targetAccessor = AbstractDataAccessor.create(this.target, factory.tables);
        this.listeners = new ArrayList<TaskExecutorListener>();
        this.deleteTarget = true;

        this.tableRows = new TreeMap<String, List<String>>();
    }

    public String getName() {
        return this.name;
    }

    public void setDeleteTarget(boolean deleteTarget) {
        this.deleteTarget = deleteTarget;
    }

    public boolean isDeleteTarget() {
        return this.deleteTarget;
    }

    /**
     *
     * @param listener
     */
    public void addListener(TaskExecutorListener listener) {
        if (listener == null) {
            return;
        }
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);

        }
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @return Result.
     * @throws SQLException SQL exception.
     */
    public synchronized boolean run(Where[] wheres) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());
            }
            if (runTask(this.task1, wheres, "/")) {
                raiseDone();
                return true;
            }
            else {
                return false;
            }
        }
        finally {
            try {
                this.sourceAccessor.disconnect();
                this.targetAccessor.disconnect();
            }
            catch (Exception ex) {

            }
        }
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @return Result.
     * @throws SQLException SQL exception.
     */
    public synchronized boolean run(Map<String, Object> whereValues) throws SQLException {
        this.tableRows.clear();
        try {
            this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
            if (this.target != null) {  // TODO: good?
                this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());
            }
            if (runTask(this.task1, whereValues, "/")) {
                raiseDone();
                return true;
            }
            else {
                return false;
            }
        }
        finally {
            try {
                this.sourceAccessor.disconnect();
                this.targetAccessor.disconnect();
            }
            catch (Exception ex) {

            }
        }
    }

    /**
     * Run this task.
     * @param whereValues Criteria used to select data from database.
     * @return Result.
     */
    private boolean runTask(TaskType task, Where[] wheres, String parentPath) {
        String statement = null;
        Map<String, Object> statementParams = null;
        Database database = Database.SOURCE;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();
            final TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, wheres);
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, wheres);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    wheres,
                    sourceResult.size(),
                    database));

            List<ColumnType> sourcePK = null;
            String sourceDelete = null;
            if (sourceSelect.isDeleted()) {
                sourcePK = findPK(sourceColumns);
                sourceDelete = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            }

            // target: delete & insert
            database = Database.TARGET;
            for (Map<String, Object> row : sourceResult) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
                List<ColumnType> targetColumns = null;
                if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                    targetColumns = sourceColumns;
                }
                else {
                    targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                }
                if (targetColumns.size() == 0) {
                    throw new SQLException(tableName + " without columns.");
                }
                List<ColumnType> pk = findPK(targetColumns);
                if (pk.size() == 0) {
                    throw new SQLException(tableName + " without primary key.");
                }

                // check if sync already
                List<String> kss = this.tableRows.get(task.getName());
                if (kss == null) {
                    kss = new ArrayList<String>();
                    this.tableRows.put(task.getName(), kss);
                }
                String keyString = statementParams.toString();
                if (kss.contains(keyString)) {
                    continue;
                }
                this.tableRows.get(task.getName()).add(keyString);

                if (this.deleteTarget) {
                    statement = AbstractDataAccessor.sqlDelete(tableName, pk);
                    statementParams = prepare(row, pk);
                    int count = this.targetAccessor.execueUpdate(statement, statementParams);
                    raiseTargetDeleted(new TaskExecutorEvent(
                            task,
                            parentPath,
                            statement,
                            statementParams,
                            count,
                            database));
                }

                statement = AbstractDataAccessor.sqlInsert(tableName, targetColumns);
                statementParams = prepare(row, targetColumns);
                int uc = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetInserted(new TaskExecutorEvent(
                        task,
                        parentPath,
                        statement,
                        statementParams,
                        uc,
                        database));

                // next plans
                if (task.getNext() != null) {
                    for (PlanType p : task.getNext().getPlan()) {
                        if (!runPlan(p, row, parentPath + task.getName() + "/")) {
                            return false;
                        }
                    }
                }

                // delete source
                database = Database.SOURCE;
                if (sourceSelect.isDeleted()) {
                    statementParams = prepare(row, sourcePK);
                    int count = this.sourceAccessor.execueUpdate(sourceDelete, statementParams);
                    raiseSourceDeleted(new TaskExecutorEvent(
                            task,
                            parentPath,
                            sourceDelete,
                            statementParams,
                            count,
                            database));
                }
            }

            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, statementParams, 0, database),
                    ex);
            return false;
        }
    }

    public void printRunLog() {
        for (Map.Entry<String, List<String>> e : this.tableRows.entrySet()) {
            System.out.println(e.getKey() + "(" + e.getValue().size() + ")");
            for (String key : e.getValue()) {
                System.out.println("  " + key);
            }
        }
    }

    /**
     * Run this task.
     * @param task task to be run.
     * @param whereValues Criteria used to select data from database.
     * @param parentPath
     * @return Result.
     */
    private boolean runTask(TaskType task, Map<String, Object> whereValues, String parentPath) {
        String statement = null;
        Map<String, Object> statementParams = whereValues == null ? new TreeMap<String, Object>() : whereValues;

        Database database = Database.SOURCE;
        try {
            SourceSelectType sourceSelect = task.getSourceSelect();
            TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, statementParams.keySet().toArray(new String[0]));
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    statementParams,
                    sourceResult.size(),
                    database));

            List<ColumnType> sourcePK = null;
            String sourceDelete = null;
            if (sourceSelect.isDeleted()) {
                sourcePK = findPK(sourceColumns);
                sourceDelete = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            }

            // target: delete & insert
            database = Database.TARGET;
            for (Map<String, Object> row : sourceResult) {
                String tableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
                List<ColumnType> targetColumns = null;
                if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                    targetColumns = sourceColumns;
                }
                else {
                    targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                }
                if (targetColumns.size() == 0) {
                    throw new SQLException(tableName + " without columns.");
                }
                List<ColumnType> pk = findPK(targetColumns);
                if (pk.size() == 0) {
                    throw new SQLException(tableName + " without primary key.");
                }

                // check if sync already
                List<String> kss = this.tableRows.get(task.getName());
                if (kss == null) {
                    kss = new ArrayList<String>();
                    this.tableRows.put(task.getName(), kss);
                }
                String keyString = statementParams.toString();
                if (kss.contains(keyString)) {
                    continue;
                }
                this.tableRows.get(task.getName()).add(keyString);

                if (this.deleteTarget) {
                    statement = AbstractDataAccessor.sqlDelete(tableName, pk);
                    statementParams = prepare(row, pk);
                    int count = this.targetAccessor.execueUpdate(statement, statementParams);
                    raiseTargetDeleted(new TaskExecutorEvent(
                            task,
                            parentPath,
                            statement,
                            statementParams,
                            count,
                            database));
                }

                statement = AbstractDataAccessor.sqlInsert(tableName, targetColumns);
                statementParams = prepare(row, targetColumns);
                int uc = this.targetAccessor.execueUpdate(statement, statementParams);
                raiseTargetInserted(new TaskExecutorEvent(
                        task,
                        parentPath,
                        statement,
                        statementParams,
                        uc,
                        database));

                // next plans
                if (task.getNext() != null) {
                    for (PlanType p : task.getNext().getPlan()) {
                        if (!runPlan(p, row, parentPath + task.getName() + "/")) {
                            return false;
                        }
                    }
                }

                // delete source
                database = Database.SOURCE;
                if (sourceSelect.isDeleted()) {
                    statementParams = prepare(row, sourcePK);
                    int count = this.sourceAccessor.execueUpdate(sourceDelete, statementParams);
                    raiseSourceDeleted(new TaskExecutorEvent(
                            task,
                            parentPath,
                            sourceDelete,
                            statementParams,
                            count,
                            database));
                }
            }
            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(new TaskExecutorEvent(task, parentPath, statement, statementParams, 0, database), ex);
            return false;
        }
    }

    private boolean runPlan(PlanType plan, Map<String, Object> master, String parentPath) throws SQLException {
        if (plan.getRule() != null && plan.getRule().getCriteria().size() > 0) {
            for (CriteriaType criteria : plan.getRule().getCriteria()) {
                Object v = master.get(criteria.getColumn());
                if (v == null) {
                    return true;
                }

                boolean accept = true;
                if (criteria.getValidate().equalsIgnoreCase("STARTSWITH")) {
                    accept = v.toString().startsWith(criteria.getValue());
                }
                else {
                    accept = v.toString().equals(criteria.getValue());
                }
                if (!accept) {
                    return true;
                }
            }
        }

        TaskType task = this.factory.tasks.get(plan.getTaskName());
        if (task == null) {
            // System.out.println(plan.getTaskName() + " no task");
        }
        Map<String, Object> whereValues = prepare(master, plan.getJoin().getColumn());

        if (plan.getWhere() != null && plan.getWhere().getCriteria() != null) {
            List<CriteriaType> where = plan.getWhere().getCriteria();
            for (CriteriaType criteria : where) {
                if (criteria.isEmplyIsNull() && (criteria.getValue() == null || "".equals(criteria.getValue()))) {
                    whereValues.put(criteria.getColumn(), null);
                }
                else {
                    whereValues.put(criteria.getColumn(), criteria.getValue());
                }
            }
        }

        return runTask(task, whereValues, parentPath);
    }

    private Map<String, Object> prepare(Map<String, Object> row, List<ColumnType> columns) throws SQLException {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        for (ColumnType column : columns) {
            if (column.getSource() == null) {
                result.put(column.getValue(), row.get(column.getValue()));
            }
            else {
                result.put(column.getValue(), row.get(column.getSource()));
            }
        }
        return result;
    }

    private List<ColumnType> findPK(List<ColumnType> columns) {
        ArrayList<ColumnType> pk = new ArrayList<ColumnType>();
        for (ColumnType column : columns) {
            if (column.isPk()) {
                pk.add(column);
            }
        }
        return pk;
    }

    private void raiseSourceSelected(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.sourceSelected(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseSourceDeleted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.sourceDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseTargetInserted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetInserted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseTargetDeleted(TaskExecutorEvent evt) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.targetDeleted(this, evt);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseExecuteFailure(TaskExecutorEvent evt, SQLException ex) {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.executeFailure(this, evt, ex);
            }
            catch (Exception ex2) {

            }
        }
    }

    private void raiseDone() {
        for (TaskExecutorListener l : this.listeners) {
            try {
                l.done(this);
            }
            catch (Exception ex2) {

            }
        }
    }
}
