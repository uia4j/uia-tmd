package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxPool.class);

    private final String jobName;

    private LinkedHashMap<String, List<Map<String, Object>>> insertTarget;

    private LinkedHashMap<String, List<Map<String, Object>>> deleteSource;

    public TxPool(String jobName) {
        this.jobName = jobName;
        this.insertTarget = new LinkedHashMap<String, List<Map<String, Object>>>();
        this.deleteSource = new LinkedHashMap<String, List<Map<String, Object>>>();
    }

    public void clear() {
        this.insertTarget.clear();
        this.deleteSource.clear();
    }

    public synchronized void insert(String tableName, List<Map<String, Object>> rows) {
        if (tableName == null || rows == null || rows.isEmpty()) {
            return;
        }

        List<Map<String, Object>> table = this.insertTarget.get(tableName);
        if (table == null) {
            table = new ArrayList<Map<String, Object>>();
            this.insertTarget.put(tableName, table);
        }

        table.addAll(rows);
    }

    public synchronized void delete(String tableName, List<Map<String, Object>> rows) {
        if (tableName == null || rows == null || rows.isEmpty()) {
            return;
        }

        List<Map<String, Object>> table = this.deleteSource.get(tableName);
        if (table == null) {
            table = new ArrayList<Map<String, Object>>();
            this.deleteSource.put(tableName, table);
        }

        table.addAll(rows);
    }

    public synchronized void commitInsert(DataAccess access) throws SQLException {
        LOGGER.info("tmd> " + this.jobName + "> target batch insert:" + this.insertTarget.size());
        if(this.insertTarget.size() == 0) {
        	return;
        }
        String tableName = null;
        try {
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.insertTarget.entrySet()) {
                tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
                access.delete(tableName, rows);
            	access.insert(tableName, rows);
            }
            access.commit();
            LOGGER.info("tmd> " + this.jobName + "> commit");
        }
        catch (SQLException ex) {
            LOGGER.error("tmd> " + this.jobName + "> rollback: " + tableName);
            access.rollback();
            ex.printStackTrace();
            throw ex;
        }
    }

    public synchronized void commitDelete(DataAccess access) throws SQLException {
        LOGGER.info("tmd> " + this.jobName + "> source batch delete:" + this.deleteSource.size());
        if(this.deleteSource.size() == 0) {
        	return;
        }
        String tableName = null;
        try {
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.deleteSource.entrySet()) {
                tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
            	access.delete(tableName, rows);
            }
            access.commit();
            LOGGER.info("tmd> " + this.jobName + "> commit");
        }
        catch (SQLException ex) {
            LOGGER.error("tmd> " + this.jobName + "> rollback: " + tableName);
            access.rollback();
            throw ex;
        }
    }
}
