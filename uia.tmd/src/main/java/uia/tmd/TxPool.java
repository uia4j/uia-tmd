
package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxPool.class);

    private final String jobName;

    private LinkedHashMap<String, Set<String>> insertPKs;

    private LinkedHashMap<String, List<Map<String, Object>>> insertTarget;

    private LinkedHashMap<String, List<Map<String, Object>>> deleteSource;
    
    private long time;

    public TxPool(String jobName) {
        this.jobName = jobName;
        this.insertPKs = new LinkedHashMap<>();
        this.insertTarget = new LinkedHashMap<>();
        this.deleteSource = new LinkedHashMap<>();
        this.time = System.currentTimeMillis();
    }

    public void done(String txId) {
        this.insertTarget.clear();
        this.deleteSource.clear();
        LOGGER.info(String.format("tmd> %s> %s spend %.3f secs", 
        		this.jobName,
        		txId,
	    		(System.currentTimeMillis() - this.time) / 1000.0d));
    	LOGGER.info(String.format("tmd> memory: %sMB/%sMB",
				Runtime.getRuntime().totalMemory() /1024 /1024,
				Runtime.getRuntime().maxMemory() /1024 /1024));
        this.time = System.currentTimeMillis();
    	System.gc();
    }

    public synchronized boolean accept(String tableName, String pk) {
        if (tableName == null || pk == null) {
            return false;
        }

        Set<String> tablePKs = this.insertPKs.get(tableName);
        if (tablePKs == null) {
        	tablePKs = new TreeSet<>();
            this.insertPKs.put(tableName, tablePKs);
        }
        
        return tablePKs.add(pk);
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
        if(this.insertTarget.size() == 0) {
            LOGGER.info("tmd> " + this.jobName + "> insert rows=0");
        	return;
        }
        String tableName = null;
        try {
            int total = 0;
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.insertTarget.entrySet()) {
                tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
                if(rows.size() == 0) {
                	continue;
                }

                access.delete(tableName, rows);
            	int insc = access.insert(tableName, rows);
            	if(rows.size() != insc) {
            		throw new SQLException(tableName + " insert failed, count not matched");
            	}
                total += rows.size();
            }
            access.commit();
            LOGGER.info(String.format("tmd> %s> insert rows=%s, tables=%s to %s",
            		this.jobName,
            		total,
            		this.insertTarget.size(),
            		access));
        }
        catch (SQLException ex) {
            access.rollback();
            LOGGER.error("tmd> " + this.jobName + "> rollback: " + tableName, ex);
            throw ex;
        }
    }

    public synchronized void commitDelete(DataAccess access) throws SQLException {
        if(this.deleteSource.size() == 0) {
            LOGGER.info("tmd> " + this.jobName + "> delete rows=0");
        	return;
        }
        String tableName = null;
        try {
            int total = 0;
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.deleteSource.entrySet()) {
                tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
                if(rows.size() == 0) {
                	continue;
                }
            	int delc = access.delete(tableName, rows);
            	if(rows.size() != delc) {
            		throw new SQLException(tableName + " delete failed, count not matched");
            	}
                total += rows.size();
            }
            access.commit();
            LOGGER.info(String.format("tmd> %s> delete rows=%s, tables=%s to %s",
            		this.jobName,
            		total,
            		this.deleteSource.size(),
            		access));
        }
        catch (SQLException ex) {
            access.rollback();
            LOGGER.error("tmd> " + this.jobName + "> rollback: " + tableName, ex);
            throw ex;
        }
    }
}
