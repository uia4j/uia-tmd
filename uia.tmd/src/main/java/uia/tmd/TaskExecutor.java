package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.PlanType;
import uia.tmd.model.xml.TaskType;
import uia.tmd.model.xml.WhereType;

/**
 * Task executor.
 *
 * @author Kyle K. Lin
 *
 */
public class TaskExecutor {

    private TaskType task;

    private DbServerType source;

    private DbServerType target;

    private DataAccessor sourceAccessor;

    private DataAccessor targetAccessor;

    /**
     * Constructor.
     *
     * @param task Task definition.
     * @param source Source.
     * @param target tARGET.
     * @throws Exception
     */
    TaskExecutor(TaskType task, DbServerType source, DbServerType target) throws Exception {
        this.task = task;
        this.source = source;
        this.target = target;
        this.sourceAccessor = createMSSQL(this.source);
        this.targetAccessor = createMSSQL(this.target);
    }

    /**
     * Run this task.
     * @param where Criteria used to select data from database.
     * @throws SQLException SQL exception.
     */
    public void run(Map<String, Object> where) throws SQLException {
        this.sourceAccessor.connect(this.source.getUser(), this.source.getPassword());
        this.targetAccessor.connect(this.target.getUser(), this.target.getPassword());

        try {
            List<Object> selectCriteria = prepare(where, this.task.getSourceSelect().getWhere().getColumn());
            List<Map<String, Object>> table = this.sourceAccessor.select(this.task.getSourceSelect().getSql(), selectCriteria);
            for (Map<String, Object> row : table) {
                List<Object> deleteCriteria = prepare(row, this.task.getTargetInsert().getDelWhere().getColumn());
                this.targetAccessor.execueUpdate(this.task.getTargetInsert().getDelSql(), deleteCriteria);
                this.targetAccessor.execueUpdate(this.task.getTargetInsert().getSql(), new ArrayList<Object>(row.values()));
                if (this.task.getNexts() != null) {
                    this.task.getNexts().getPlan().stream().forEach(p -> {
                        try {
                            run(p, row);
                        }
                        catch (Exception ex) {

                        }
                    });
                }
            }
        }
        finally {
            this.sourceAccessor.disconnect();
            this.targetAccessor.disconnect();
        }
    }

    private void run(PlanType plan, Map<String, Object> master) throws SQLException {
        List<Object> selectCriteria = prepare(master, plan.getSourceSelect().getWhere().getColumn());
        List<Map<String, Object>> table = this.sourceAccessor.select(plan.getSourceSelect().getSql(), selectCriteria);
        for (Map<String, Object> row : table) {
            List<Object> deleteCriteria = prepare(row, plan.getTargetInsert().getDelWhere().getColumn());
            this.targetAccessor.execueUpdate(plan.getTargetInsert().getDelSql(), deleteCriteria);
            this.targetAccessor.execueUpdate(plan.getTargetInsert().getSql(), new ArrayList<Object>(row.values()));

            if (plan.getNexts() != null) {
                plan.getNexts().getPlan().stream().forEach(p -> {
                    try {
                        run(p, row);
                    }
                    catch (Exception ex) {

                    }
                });
            }
        }
    }

    private DataAccessor createMSSQL(DbServerType dbServer) throws Exception {
        if ("MSSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new MSSQLAccessor(dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
        }
        else if ("PGSQL".equalsIgnoreCase(dbServer.getDbType())) {
            return new PGSQLAccessor(dbServer.getHost(), dbServer.getPort(), dbServer.getDbName());
        }

        return null;
    }

    private List<Object> prepare(Map<String, Object> value, List<WhereType> where) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (WhereType w : where) {
            if (w.getSource() == null) {
                result.add(value.get(w.getValue()));
            }
            else {
                result.add(value.get(w.getSource()));
            }
        }
        return result;
    }
}
