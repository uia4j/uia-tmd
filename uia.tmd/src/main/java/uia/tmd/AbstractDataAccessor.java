package uia.tmd;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.TableType;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public abstract class AbstractDataAccessor implements DataAccessor {

    protected DbServerType svrType;

    protected Map<String, TableType> tables;

    protected TreeMap<String, List<ColumnType>> tableColumns;

    protected AbstractDataAccessor() {
        this.tableColumns = new TreeMap<String, List<ColumnType>>();
    }

    @Override
    public void initial(DbServerType svrType, Map<String, TableType> tables) {
        this.svrType = svrType;
        this.tables = tables;
    }

    /**
     * Get connection.
     * @return Connection.
     */
    @Override
    public abstract Connection getConnection();

    /**
     * Connect to database.
     * @param user User id.
     * @param password Password.
     * @throws SQLException SQL exception.
     */
    @Override
    public abstract void connect() throws SQLException;

    /**
     * Disconnect to database.
     * @throws SQLException SQL exception.
     */
    @Override
    public abstract void disconnect() throws SQLException;

    /**
     * Switch to manual commit. Use commit() to commit statements.
     * @throws SQLException SQL exception.
     */
    @Override
    public void beingTx() throws SQLException {
        getConnection().setAutoCommit(true);
    }

    /**
     * Switch to auto commit.
     * @throws SQLException SQL exception.
     */
    @Override
    public void endTx() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * Commit.
     * @throws SQLException SQL exception.
     */
    @Override
    public void commit() throws SQLException {
        getConnection().commit();
    }

    /**
     * Rollback.
     * @throws SQLException SQL exception.
     */
    @Override
    public void rollback() throws SQLException {
        getConnection().rollback();
    }

    @Override
    public List<String> listTables() throws SQLException {
        ArrayList<String> tables = new ArrayList<String>();
        ResultSet rs = getConnection().getMetaData().getTables(null, null, null, new String[] { "TABLE" });
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

    /**
     * Select data.
     * @param sql SQL statement.
     * @param wheres Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    @Override
    public List<Map<String, Object>> select(String sql, Where[] wheres) throws SQLException {
        ArrayList<Map<String, Object>> table = new ArrayList<Map<String, Object>>();

        // parameters
        PreparedStatement ps = getConnection().prepareStatement(sql);
        if (wheres != null) {
            int index = 1;
            for (Where where : wheres) {
                index = where.addParameters(ps, index);
            }
        }

        // select
        ResultSet rs = ps.executeQuery();
        int cnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            for (int i = 0; i < cnt; i++) {
                if (rs.getMetaData().getColumnType(i + 1) == 2005) {
                    row.put(rs.getMetaData().getColumnName(i + 1), convert(rs.getClob(i + 1)));
                }
                else {
                    row.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));
                }
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
     * @return Count of updated records.
     * @throws SQLException SQL exception.
     * @throws IOException
     */
    @Override
    public int execueUpdate(String sql, Map<String, Object> parameters) throws SQLException {
        if (parameters == null) {
            return 0;
        }

        PreparedStatement ps = getConnection().prepareStatement(sql);
        int i = 1;
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            if (e.getValue() instanceof ClobData) {
                ClobData data = (ClobData) e.getValue();
                try {
                    Clob clob = getConnection().createClob();
                    clob.setString(1, data.content);
                    ps.setClob(i, clob);
                }
                catch (Exception ex) {
                    ps.setString(i, data.content);
                }
            }
            else {
                ps.setObject(i, e.getValue());
            }
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
     * @throws SQLException SQL exception.
     */
    @Override
    public int execueBatch(String sql, List<Map<String, Object>> table) throws SQLException {
        if (table == null || table.size() == 0) {
            return 0;
        }

        int batchSize = 200;
        int count = 0;
        PreparedStatement ps = getConnection().prepareStatement(sql);
        for (Map<String, Object> parameters : table) {
            int i = 1;
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                if (e.getValue() instanceof ClobData) {
                    ClobData data = (ClobData) e.getValue();
                    try {
                        Clob clob = getConnection().createClob();
                        clob.setString(1, data.content);
                        ps.setClob(i, clob);
                    }
                    catch (Exception ex) {
                        ps.setString(i, data.content);
                    }
                }
                else {
                    ps.setObject(i, e.getValue());
                }
                i++;
            }
            ps.addBatch();

            count++;
            if ((count % batchSize) == 0) {
                ps.executeBatch();
            }
        }

        ps.executeBatch();
        ps.close();

        return count;
    }

    @Override
    public List<ColumnType> prepareColumns(String tableName) throws SQLException {
        List<ColumnType> cts = this.tableColumns.get(tableName);
        if (cts != null) {
            return cts;
        }
        cts = new ArrayList<ColumnType>();

        Connection conn = getConnection();
        TableType tt = this.tables.get(tableName);

        // primary key
        List<String> pk;
        if (tt != null && tt.getPk() != null && tt.getPk().getName().size() > 0) {
            pk = tt.getPk().getName();
        }
        else {
            pk = new ArrayList<String>();
            ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                pk.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
        }

        ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, null);
        while (rs.next()) {
            if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                String columnName = rs.getString("COLUMN_NAME");
                ColumnType ct = new ColumnType();
                ct.setPk(pk.contains(columnName));
                ct.setValue(columnName);
                cts.add(ct);
            }
        }
        rs.close();

        this.tableColumns.put(tableName, cts);

        return cts;
    }

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

    static String sqlSelect(String table, List<ColumnType> columns, Where[] where) {
        // fields
        StringBuilder sb1 = new StringBuilder(columns.get(0).getValue());
        for (int i = 1, n = columns.size(); i < n; i++) {
            sb1.append(",").append(columns.get(i).getValue());
        }
        // where
        if (where.length > 0) {
            StringBuilder sb2 = new StringBuilder(where[0].sql());
            for (int i = 1, n = where.length; i < n; i++) {
                sb2.append(" AND ").append(where[i].sql());
            }
            return String.format("SELECT %s FROM %s WHERE %s", sb1, table, sb2);
        }
        else {
            return String.format("SELECT %s FROM %s", sb1, table);
        }

    }

    static String sqlSelect(String table, List<ColumnType> columns, String[] where) {
        // fields
        StringBuilder sb1 = new StringBuilder(columns.get(0).getValue());
        for (int i = 1, n = columns.size(); i < n; i++) {
            sb1.append(",").append(columns.get(i).getValue());
        }
        // where
        if (where.length > 0) {
            StringBuilder sb2 = new StringBuilder(where[0]).append("=?");
            for (int i = 1, n = where.length; i < n; i++) {
                sb2.append(" AND ").append(where[i]).append("=?");
            }
            return String.format("SELECT %s FROM %s WHERE %s", sb1, table, sb2);
        }
        else {
            return String.format("SELECT %s FROM %s", sb1, table);
        }

    }

    static String sqlDelete(String table, List<ColumnType> where) {
        // where
        StringBuilder sb2 = new StringBuilder(where.get(0).getValue()).append("=?");
        for (int i = 1, n = where.size(); i < n; i++) {
            sb2.append(" AND ").append(where.get(i).getValue()).append("=?");
        }

        return String.format("DELETE FROM %s WHERE %s", table, sb2);
    }

    private ClobData convert(Clob clob) throws SQLException {
        StringBuffer sb = new StringBuffer((int) clob.length());
        Reader r = clob.getCharacterStream();
        char[] cbuf = new char[2048];
        int n = 0;
        try {
            while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
                if (n > 0) {
                    sb.append(cbuf, 0, n);
                }
            }
        }
        catch (IOException e1) {
            throw new SQLException(e1);
        }

        ClobData data = new ClobData();
        data.content = sb.toString();
        return data;

    }
}
