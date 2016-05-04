package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uia.tmd.TaskExecutorListener.TaskExecutorEvent;
import uia.tmd.TaskExecutorListener.TaskExecutorEvent.Database;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.ExecutorType;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TaskType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public class ConcurrentTaskExecutor extends TaskExecutor {

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param factory Task factory.
     * @throws Exception
     */
    ConcurrentTaskExecutor(TaskFactory factory, ExecutorType executor) throws Exception {
        super(factory, executor);
    }

    @Override
    protected boolean runTask(final TaskType task, Where[] wheres, final String parentPath) {
        List<String> kss = this.tableRows.get(task.getName());
        if (kss == null) {
            kss = new ArrayList<String>();
            this.tableRows.put(task.getName(), kss);
        }

        Database database = Database.SOURCE;
        String statement = null;
        try {
            final SourceSelectType sourceSelect = task.getSourceSelect();
            final TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            final List<ColumnType> sourcePK = findPK(sourceColumns);
            if (sourcePK.size() == 0) {
                throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
            }

            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, wheres);
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, wheres);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    wheres,
                    sourceResult.size(),
                    database));

            // target: delete & insert
            final String targetTableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
            final List<ColumnType> targetColumns;
            if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                targetColumns = sourceColumns;
            }
            else {
                targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                // TODO: fix
                List<ColumnType> temp = targetUpdate.getColumns().getColumn();
                for (ColumnType temp0 : temp) {
                    for (ColumnType temp2 : targetColumns) {
                        if (temp2.getValue().equals(temp0.getValue())) {
                            temp2.setSource(temp0.getSource());
                            break;
                        }
                    }
                }
            }
            if (targetColumns.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without columns.");
            }
            final List<ColumnType> targetPK = findPK(targetColumns);
            if (targetPK.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without primary key.");
            }

            ExecutorService serv = Executors.newFixedThreadPool(8);
            LinkedHashMap<Map<String, Object>, Future<Integer>> fs = new LinkedHashMap<Map<String, Object>, Future<Integer>>();

            for (final Map<String, Object> row : sourceResult) {
                Future<Integer> f = serv.submit(new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        Map<String, Object> sourcePKvalues = prepare(row, sourcePK);
                        return handle(task, sourcePKvalues.toString(), targetTableName, targetColumns, targetPK, parentPath, row);
                    }

                });
                fs.put(row, f);
            }

            ArrayList<Map<String, Object>> needNext = new ArrayList<Map<String, Object>>();
            for (Map.Entry<Map<String, Object>, Future<Integer>> kvp : fs.entrySet()) {
                Map<String, Object> row = kvp.getKey();
                Future<Integer> f = kvp.getValue();
                try {
                    int rc = f.get().intValue();
                    if (rc < 0) {
                        return false;
                    }
                    else if (rc > 0) {
                        needNext.add(row);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            fs.clear();
            serv.shutdown();

            // next plans
            for (Map<String, Object> row : needNext) {
                if (!runNext(task, row, parentPath)) {
                    return false;
                }
            }

            // delete source
            database = Database.SOURCE;
            if (!sourceSelect.isDeleted()) {
                return true;
            }

            serv = Executors.newFixedThreadPool(8);

            final String sourceDelete = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            ArrayList<Future<Integer>> fs2 = new ArrayList<Future<Integer>>();
            for (final Map<String, Object> row : sourceResult) {
                Future<Integer> f = serv.submit(new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        Map<String, Object> statementParams = prepare(row, sourcePK);
                        int count = ConcurrentTaskExecutor.this.sourceAccessor.execueUpdate(sourceDelete, statementParams);
                        raiseSourceDeleted(new TaskExecutorEvent(
                                task,
                                parentPath,
                                sourceDelete,
                                statementParams,
                                count,
                                Database.SOURCE));
                        return count;
                    }

                });
                fs2.add(f);
            }
            for (Future<Integer> f : fs2) {
                try {
                    f.get();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            serv.shutdown();

            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(
                    new TaskExecutorEvent(task, parentPath, statement, wheres, 0, database),
                    ex);
            return false;
        }
    }

    /**
     * Run this task.
     * @param task task to be run.
     * @param whereValues Criteria used to select data from database.
     * @param parentPath
     * @return Result.
     */
    @Override
    protected boolean runTask(final TaskType task, Map<String, Object> whereValues, final String parentPath) {
        List<String> kss = this.tableRows.get(task.getName());
        if (kss == null) {
            kss = new ArrayList<String>();
            this.tableRows.put(task.getName(), kss);
        }

        Database database = Database.SOURCE;
        String statement = null;
        Map<String, Object> statementParams = whereValues == null ? new TreeMap<String, Object>() : whereValues;
        try {
            SourceSelectType sourceSelect = task.getSourceSelect();
            TargetUpdateType targetUpdate = task.getTargetUpdate();

            // source: select
            final List<ColumnType> sourceColumns = this.sourceAccessor.prepareColumns(sourceSelect.getTable());
            final List<ColumnType> sourcePK = findPK(sourceColumns);
            if (sourcePK.size() == 0) {
                throw new SQLException("Table:" + sourceSelect.getTable() + " without primary key.");
            }

            statement = AbstractDataAccessor.sqlSelect(sourceSelect.getTable(), sourceColumns, statementParams.keySet().toArray(new String[0]));
            List<Map<String, Object>> sourceResult = this.sourceAccessor.select(statement, statementParams);
            raiseSourceSelected(new TaskExecutorEvent(
                    task,
                    parentPath,
                    statement,
                    statementParams,
                    sourceResult.size(),
                    database));

            // target: delete & insert
            final String targetTableName = targetUpdate.getTable() == null ? sourceSelect.getTable() : targetUpdate.getTable();
            final List<ColumnType> targetColumns;
            if (targetUpdate.getColumns() == null || targetUpdate.getColumns().getColumn().size() == 0) {
                targetColumns = sourceColumns;
            }
            else {
                targetColumns = this.targetAccessor.prepareColumns(targetUpdate.getTable());
                // TODO: fix
                List<ColumnType> temp = targetUpdate.getColumns().getColumn();
                for (ColumnType temp0 : temp) {
                    for (ColumnType temp2 : targetColumns) {
                        if (temp2.getValue().equals(temp0.getValue())) {
                            temp2.setSource(temp0.getSource());
                            break;
                        }
                    }
                }
            }
            if (targetColumns.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without columns.");
            }
            final List<ColumnType> targetPK = findPK(targetColumns);
            if (targetPK.size() == 0) {
                throw new SQLException("Table:" + targetTableName + " without primary key.");
            }

            ExecutorService serv = Executors.newFixedThreadPool(8);
            LinkedHashMap<Map<String, Object>, Future<Integer>> fs = new LinkedHashMap<Map<String, Object>, Future<Integer>>();

            database = Database.TARGET;
            for (final Map<String, Object> row : sourceResult) {
                Future<Integer> f = serv.submit(new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        Map<String, Object> sourcePKvalues = prepare(row, sourcePK);
                        return handle(task, sourcePKvalues.toString(), targetTableName, targetColumns, targetPK, parentPath, row);
                    }

                });
                fs.put(row, f);
            }

            ArrayList<Map<String, Object>> needNext = new ArrayList<Map<String, Object>>();
            for (Map.Entry<Map<String, Object>, Future<Integer>> kvp : fs.entrySet()) {
                Map<String, Object> row = kvp.getKey();
                Future<Integer> f = kvp.getValue();
                try {
                    int rc = f.get().intValue();
                    if (rc < 0) {
                        return false;
                    }
                    else if (rc > 0) {
                        needNext.add(row);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            fs.clear();
            serv.shutdown();

            // next plans
            database = Database.TARGET;
            for (Map<String, Object> row : needNext) {
                if (!runNext(task, row, parentPath)) {
                    return false;
                }
            }

            // delete source
            database = Database.SOURCE;
            if (!sourceSelect.isDeleted()) {
                return true;
            }

            serv = Executors.newFixedThreadPool(8);

            final String sourceDelete = AbstractDataAccessor.sqlDelete(sourceSelect.getTable(), sourcePK);
            ArrayList<Future<Integer>> fs2 = new ArrayList<Future<Integer>>();
            for (final Map<String, Object> row : sourceResult) {
                Future<Integer> f = serv.submit(new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        Map<String, Object> statementParams = prepare(row, sourcePK);
                        int count = ConcurrentTaskExecutor.this.sourceAccessor.execueUpdate(sourceDelete, statementParams);
                        raiseSourceDeleted(new TaskExecutorEvent(
                                task,
                                parentPath,
                                sourceDelete,
                                statementParams,
                                count,
                                Database.SOURCE));
                        return count;
                    }

                });
                fs2.add(f);
            }
            for (Future<Integer> f : fs2) {
                try {
                    f.get();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            serv.shutdown();

            return true;
        }
        catch (SQLException ex) {
            raiseExecuteFailure(new TaskExecutorEvent(task, parentPath, statement, statementParams, 0, database), ex);
            return false;
        }
    }
}
