package uia.tmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.ColumnType;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public abstract class DataAccessor {

    /**
     * Get connection.
     * @return Connection.
     */
    public abstract Connection getConnection();

    /**
     * Connect to database.
     * @param user User id.
     * @param password Password.
     * @throws SQLException SQL exception.
     */
    public abstract void connect(String user, String password) throws SQLException;

    /**
     * Disconnect to database.
     * @throws SQLException SQL exception.
     */
    public abstract void disconnect() throws SQLException;

    /**
     * Switch to manual commit. Use commit() to commit statements.
     * @throws SQLException SQL exception.
     */
    public void beingTx() throws SQLException {
        getConnection().setAutoCommit(true);
    }

    /**
     * Switch to auto commit.
     * @throws SQLException SQL exception.
     */
    public void endTx() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * Commit.
     * @throws SQLException SQL exception.
     */
    public void commit() throws SQLException {
        getConnection().commit();
    }

    /**
     * Rollback.
     * @throws SQLException SQL exception.
     */
    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    /**
     * Select data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String sql, Map<String, Object> parameters) throws SQLException {
        ArrayList<Map<String, Object>> table = new ArrayList<Map<String, Object>>();

        // parameters
        PreparedStatement ps = getConnection().prepareStatement(sql);
        if (parameters != null) {
            int i = 1;
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                ps.setObject(i, e.getValue());
                i++;
            }
        }

        // select
        ResultSet rs = ps.executeQuery();
        int cnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            for (int i = 0; i < cnt; i++) {
                row.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));
            }
            table.add(row);
        }

        rs.close();
        ps.close();

        return table;
    }

    /**
     * Insert, update or delete data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Count of updated recored.
     * @throws SQLException SQL exception.
     */
    public int execueUpdate(String sql, Map<String, Object> parameters) throws SQLException {
        if (parameters == null) {
            return 0;
        }

        PreparedStatement ps = getConnection().prepareStatement(sql);
        int i = 1;
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            ps.setObject(i, e.getValue());
            i++;
        }

        int rc = ps.executeUpdate();
        ps.close();

        return rc;
    }

    /**
     * Insert, update or delete data.
     * @param sql SQL statement.
     * @param table Values of parameters with ordering.
     * @return Count of updated recored.
     * @throws SQLException SQL exception.
     */
    public void execueUpdateBatch(String sql, List<Map<String, Object>> table) throws SQLException {
        if (table == null || table.size() == 0) {
            return;
        }

        int batchSize = 100;
        int count = 0;
        PreparedStatement ps = getConnection().prepareStatement(sql);
        for (Map<String, Object> parameters : table) {
            int i = 1;
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                ps.setObject(i, e.getValue());
                i++;
            }
            ps.addBatch();

            if (++count % batchSize == 0) {
                ps.executeBatch();
            }
        }

        ps.executeBatch();
        ps.close();
    }

    /**
     *
     * @param table
     * @param columns
     * @return
     */
    static String sqlInsert(String table, List<ColumnType> columns) {
        if (columns.size() == 0) {
            return null;
        }
        StringBuilder sb1 = new StringBuilder(columns.get(0).getValue());
        StringBuilder sb2 = new StringBuilder("?");
        for (int i = 1, n = columns.size(); i < n; i++) {
            sb1.append(",").append(columns.get(i).getValue());
            sb2.append(",?");
        }
        return String.format("INSERT INTO %s(%s) VALUES(%s)", table, sb1, sb2);

    }

    /**
     *
     * @param table
     * @param columns
     * @param where
     * @return
     */
    static String sqlSelect(String table, List<ColumnType> columns, List<ColumnType> where) {
        // fields
        StringBuilder sb1 = new StringBuilder(columns.get(0).getValue());
        for (int i = 1, n = columns.size(); i < n; i++) {
            sb1.append(",").append(columns.get(i).getValue());
        }
        // where
        StringBuilder sb2 = new StringBuilder(where.get(0).getValue()).append("=?");
        for (int i = 1, n = where.size(); i < n; i++) {
            sb2.append(" AND ").append(where.get(i).getValue()).append("=?");
        }

        return String.format("SELECT %s FROM %s WHERE %s", sb1, table, sb2);
    }

    /**
     *
     * @param table
     * @param where
     * @return
     */
    static String sqlDelete(String table, List<ColumnType> where) {
        // where
        StringBuilder sb2 = new StringBuilder(where.get(0).getValue()).append("=?");
        for (int i = 1, n = where.size(); i < n; i++) {
            sb2.append(" AND ").append(where.get(i).getValue()).append("=?");
        }

        return String.format("DELETE FROM %s WHERE %s", table, sb2);
    }
}
