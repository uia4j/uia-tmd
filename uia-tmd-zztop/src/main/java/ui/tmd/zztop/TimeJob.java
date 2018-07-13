package ui.tmd.zztop;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import uia.tmd.JobListener;
import uia.tmd.JobRunner;
import uia.tmd.TaskFactory;
import uia.tmd.TaskListener;
import uia.tmd.zztop.db.TmdExecutorLog;
import uia.tmd.zztop.db.TmdTaskLog;
import uia.tmd.zztop.db.TmdTxKey;
import uia.tmd.zztop.db.TmdTxTable;
import uia.tmd.zztop.db.dao.TmdExecutorLogDao;
import uia.tmd.zztop.db.dao.TmdTaskLogDao;
import uia.tmd.zztop.db.dao.TmdTxKeyDao;
import uia.tmd.zztop.db.dao.TmdTxTableDao;

public class TimeJob implements JobListener, TaskListener {

    private Connection conn;

    private TmdExecutorLog executorLog;

    private TreeMap<String, String> pathKeys;

    private TmdExecutorLogDao executorLogDao;

    private TmdTaskLogDao taskLogDao;

    private TmdTxTableDao txTableDao;

    private TmdTxKeyDao txKeyDao;

    public TimeJob() throws Exception {
        this.conn = DB.create();
        this.pathKeys = new TreeMap<String, String>();

        this.executorLogDao = new TmdExecutorLogDao(this.conn);
        this.taskLogDao = new TmdTaskLogDao(this.conn);
        this.txTableDao = new TmdTxTableDao(this.conn);
        this.txKeyDao = new TmdTxKeyDao(this.conn);
    }

    public void close() throws Exception {
        this.conn.close();
    }

    public void run(String executorName, String sqlWhere) throws Exception {
        TaskFactory factory = new TaskFactory(new File("conf/tmd_plans.xml"));

        JobRunner executor = factory.createExecutor(executorName);

        this.executorLog = new TmdExecutorLog();
        this.executorLog.setTmdExecutorBo(executor.getExecutorName());
        this.executorLog.setDatabaseSource(executor.getDatabaseSource());
        this.executorLog.setDatabaseTarget(executor.getDatabaseTarget());
        try {
            this.executorLogDao.insert(this.executorLog);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        executor.addExecutorListener(this);
        executor.addTaskListener(this);
        executor.run(sqlWhere);
    }

    public void run(String executorName, String sqlWhere, List<Object> paramValues) throws Exception {
        TaskFactory factory = new TaskFactory(new File("conf/tmd_plans.xml"));

        JobRunner executor = factory.createExecutor(executorName);

        this.executorLog = new TmdExecutorLog();
        this.executorLog.setTmdExecutorBo(executor.getExecutorName());
        this.executorLog.setDatabaseSource(executor.getDatabaseSource());
        this.executorLog.setDatabaseTarget(executor.getDatabaseTarget());
        try {
            this.executorLogDao.insert(this.executorLog);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        executor.addTaskListener(this);
        executor.run(sqlWhere, paramValues);
    }

    @Override
    public void itemBegin(JobRunner executor, ExecutorEvent event) {
        try {
            TmdTxTable txTable = new TmdTxTable();
            txTable.setTxTime(executor.getTxTime());
            txTable.setTableName(event.tableName);
            this.txTableDao.insert(txTable);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void itemEnd(JobRunner executor, ExecutorEvent event) {

    }

    @Override
    public void sourceSelected(Object executor, TaskEvent evt) {
        Date txTime = new Date();

        TmdTaskLog data = new TmdTaskLog();
        data.setTmdTaskBo(evt.taskName);
        data.setTmdExecutorLogBo(this.executorLog.getId());
        data.setTableName(evt.tableName);
        data.setSqlWhere(evt.sql);
        data.setTriggeredBy(this.pathKeys.get(evt.parentPath));
        data.setResultCount(evt.count);
        data.setTaskPath(evt.getPath());

        if (this.pathKeys.isEmpty()) {
            this.executorLog.setTmdTaskLogBo(data.getId());
        }
        this.pathKeys.put(evt.getPath(), data.getId());

        try {
            this.taskLogDao.insert(data);

            ArrayList<TmdTxKey> keys = new ArrayList<TmdTxKey>();
            evt.keys.forEach(k -> {
                TmdTxKey key = new TmdTxKey();
                key.setId(k);
                key.setTxTime(txTime);
                key.setTableName(evt.tableName);
                keys.add(key);
            });
            this.txKeyDao.insert(keys);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sourceDeleted(Object executor, TaskEvent evt) {

    }

    @Override
    public void targetInserted(Object executor, TaskEvent evt) {
    }

    @Override
    public void targetDeleted(Object executor, TaskEvent evt) {

    }

    @Override
    public void executeFailure(Object executor, TaskEvent evt, SQLException ex) {
        try {
            this.executorLog.setExecutedTime(new Date());
            this.executorLog.setExecutedResult("FAILED");
            this.executorLog.setRunState("FAILED");
            this.executorLogDao.update(this.executorLog);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void done(Object executor) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(this.executorLog.getExecutedDate());
            cal.add(Calendar.MONTH, 3);

            this.executorLog.setExecutedTime(new Date());
            this.executorLog.setExecutedResult("DONE");
            this.executorLog.setRunState("WAIT_CONFIRM");
            this.executorLog.setDeleteAfter(cal.getTime());
            this.executorLogDao.update(this.executorLog);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
