package uia.tmd;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TxPool {

    private LinkedHashMap<String, List<Map<String, Object>>> insert;

    private LinkedHashMap<String, List<Map<String, Object>>> delete;

    public TxPool() {
        this.insert = new LinkedHashMap<String, List<Map<String, Object>>>();
        this.delete = new LinkedHashMap<String, List<Map<String, Object>>>();
    }

    public void clear() {
        this.insert.clear();
    }

    public synchronized void insert(String tableName, List<Map<String, Object>> rows) {
        if (tableName == null || rows == null || rows.isEmpty()) {
            return;
        }

        List<Map<String, Object>> table = this.insert.get(tableName);
        if (table == null) {
            table = new ArrayList<Map<String, Object>>();
            this.insert.put(tableName, table);
        }

        table.addAll(rows);
    }

    public synchronized void delete(String tableName, List<Map<String, Object>> rows) {
        if (tableName == null || rows == null || rows.isEmpty()) {
            return;
        }

        List<Map<String, Object>> table = this.delete.get(tableName);
        if (table == null) {
            table = new ArrayList<Map<String, Object>>();
            this.delete.put(tableName, table);
        }

        table.addAll(rows);
    }

    public synchronized void commitInsert(DataAccess access) throws SQLException {
        try {
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.insert.entrySet()) {
                String tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
                access.insert(tableName, rows);
            }
            access.commit();
        }
        catch (SQLException ex) {
            access.rollback();
            throw ex;
        }
    }

    public void commitDelete(DataAccess access) throws SQLException {
        try {
            access.beginTx();
            for (Map.Entry<String, List<Map<String, Object>>> e : this.delete.entrySet()) {
                String tableName = e.getKey();
                List<Map<String, Object>> rows = e.getValue();
                access.delete(tableName, rows);
            }
            access.commit();
        }
        catch (SQLException ex) {
            access.rollback();
            throw ex;
        }
    }
}
