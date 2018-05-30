package uia.tmd;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.MColumnType;
import uia.tmd.model.xml.MTableType;
import uia.utils.dao.ColumnType;
import uia.utils.dao.where.Statement;
import uia.utils.dao.where.Where;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public abstract class AbstractDataAccessor implements DataAccessor {

    protected DbServerType svrType;

    protected Map<String, MTableType> tables;

    protected TreeMap<String, List<MColumnType>> tableColumns;

    protected AbstractDataAccessor() {
        this.tableColumns = new TreeMap<String, List<MColumnType>>();
    }

    @Override
    public void initial(DbServerType svrType, Map<String, MTableType> tables) {
        this.svrType = svrType;
        this.tables = tables;
    }

    /**
     * Switch to manual commit. Use commit() to commit statements.
     * @throws SQLException SQL exception.
     */
    @Override
    public void beingTx() throws SQLException {
        getDatabase().getConnection().setAutoCommit(true);
    }

    /**
     * Switch to auto commit.
     * @throws SQLException SQL exception.
     */
    @Override
    public void endTx() throws SQLException {
        getDatabase().getConnection().setAutoCommit(false);
    }

    /**
     * Commit.
     * @throws SQLException SQL exception.
     */
    @Override
    public void commit() throws SQLException {
        getDatabase().getConnection().commit();
    }

    /**
     * Rollback.
     * @throws SQLException SQL exception.
     */
    @Override
    public void rollback() throws SQLException {
        getDatabase().getConnection().rollback();
    }

    @Override
    public List<String> listTables() throws SQLException {
        return getDatabase().selectTableNames();
    }

    /**
     * Select data.
     * @param sql SQL statement.
     * @param wheres Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    @Override
    public List<Map<String, Object>> select(String sql, Where where) throws SQLException {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        Statement stat = new Statement().where(where);

        // select
        PreparedStatement ps = stat.prepare(getDatabase().getConnection(), sql);
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
            rows.add(row);
        }

        rs.close();
        ps.close();

        return rows;
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

        PreparedStatement ps = getDatabase().getConnection().prepareStatement(sql);
        int i = 1;
        for (Map.Entry<String, Object> e : parameters.entrySet()) {
            if (e.getValue() instanceof ClobData) {
                ClobData data = (ClobData) e.getValue();
                try {
                    Clob clob = getDatabase().getConnection().createClob();
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
        PreparedStatement ps = getDatabase().getConnection().prepareStatement(sql);
        for (Map<String, Object> parameters : table) {
            int i = 1;
            for (Map.Entry<String, Object> e : parameters.entrySet()) {
                if (e.getValue() instanceof ClobData) {
                    ClobData data = (ClobData) e.getValue();
                    try {
                        Clob clob = getDatabase().getConnection().createClob();
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
    public List<MColumnType> prepareColumns(String tableName) throws SQLException {
        List<MColumnType> cts = this.tableColumns.get(tableName);
        if (cts != null) {
            return cts;
        }

        List<ColumnType> origs = getDatabase().selectTable(tableName, false).getColumns();
        for (ColumnType orig : origs) {
            MColumnType ct = new MColumnType();
            ct.setSource(orig.getColumnName());
            ct.setValue(orig.getColumnName());
            ct.setPk(orig.isPk());
            cts.add(ct);
        }
        this.tableColumns.put(tableName, cts);

        return cts;
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
