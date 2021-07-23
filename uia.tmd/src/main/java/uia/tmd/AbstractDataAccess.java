package uia.tmd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uia.tmd.model.xml.AbstractTableType;
import uia.tmd.model.xml.DatabaseType;
import uia.dao.ColumnType;
import uia.dao.Database;
import uia.dao.TableType;
import uia.dao.where.Where;

public abstract class AbstractDataAccess implements DataAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataAccess.class);

    protected DatabaseType databaseType;

    protected Map<String, AbstractTableType> abstractTables;

    protected Map<String, TableType> cacheTables;

    protected AbstractDataAccess() {
        this.cacheTables = new TreeMap<String, TableType>();
    }

    @Override
    public void initial(DatabaseType databaseType, Map<String, AbstractTableType> tables) {
        this.databaseType = databaseType;
        this.abstractTables = tables;
    }

    @Override
    public List<Map<String, Object>> select(String tableName, String where) throws SQLException {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		// select
        String sql = (where == null || where.trim().isEmpty())
                ? prepareTable(tableName).generateSelectSQL()
                : prepareTable(tableName).generateSelectSQL() + " WHERE " + where;
        Connection conn = getDatabase().getConnection();
        //try (Connection conn = getDatabase().createConnection()) {
            try (Statement ps = conn.createStatement()) {
                try (ResultSet rs = ps.executeQuery(sql)) {
                    int cnt = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
                        for (int i = 0; i < cnt; i++) {
                            // TODO: BLOB
                            row.put(rs.getMetaData().getColumnName(i + 1).toUpperCase(), rs.getObject(i + 1));
                        }
                        rows.add(row);
                    }
                }
            }
        //}
        catch (Exception ex) {
            LOGGER.error("failed to run:" + sql);
            throw ex;
        }

        return rows;

    }

    @Override
    public List<Map<String, Object>> select(String tableName, String where, List<Object> paramValues) throws SQLException {
        ArrayList<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        // select
        String sql = prepareTable(tableName).generateSelectSQL();
        boolean useParams = false;
        if (where != null && !where.trim().isEmpty()) {
            sql += (" WHERE " + where);
            useParams = true;
        }

        //try (Connection conn = getDatabase().createConnection()) {
            try (PreparedStatement ps = getDatabase().getConnection().prepareStatement(sql)) {
                if (useParams) {
                    for (int i = 0, c = paramValues.size(); i < c; i++) {
                        Object pv = paramValues.get(i);
                        if (pv instanceof Date) {
                            ps.setTimestamp(i + 1, new Timestamp(((Date) pv).getTime()));
                        }
                        else {
                            ps.setObject(i + 1, paramValues.get(i));
                        }
                    }
                }

                try (ResultSet rs = ps.executeQuery()) {
                    int cnt = rs.getMetaData().getColumnCount();
                    while (rs.next()) {
                        LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
                        for (int i = 0; i < cnt; i++) {
                            // TODO: BLOB
                            row.put(rs.getMetaData().getColumnName(i + 1).toUpperCase(), rs.getObject(i + 1));
                        }
                        rows.add(row);
                    }
                }
            }
        //}
        catch (Exception ex) {
            LOGGER.error("failed to run:" + Where.toString(sql, paramValues), ex);
            throw ex;
        }

        return rows;
    }

    @Override
    public synchronized int insert(String tableName, List<Map<String, Object>> rows) throws SQLException {
        if (rows == null || rows.isEmpty()) {
            return 0;
        }

        List<String> columns = new ArrayList<String>(rows.get(0).keySet());
    	columns.replaceAll(String::toUpperCase);

        TableType temp = prepareTable(tableName);
        TableType table = new TableType(temp.getTableName(), temp.getRemark(), new ArrayList<>(), true);
    	for(ColumnType column : temp.getColumns()) {
    		if(columns.contains(column.getColumnName().toUpperCase())) {
    			table.getColumns().add(column);
    		}
    	}
        try (PreparedStatement ps = getDatabase()
                .getConnection()
                .prepareStatement(table.generateInsertSQL())) {
            for (Map<String, Object> row : rows) {
                int i = 1;
                for (ColumnType ct : table.getColumns()) {
                    Object value = row.get(ct.getColumnName().toUpperCase());
                    // important
                    // CLOB, NCLOB, original
                    ps.setObject(i, ct.read(getDatabase().getConnection(), value));
                    i++;
                }
                ps.addBatch();
            }

            return ps.executeBatch().length;
        }
    }

    @Override
    public synchronized int delete(String tableName, List<Map<String, Object>> rows) throws SQLException {
        if (rows == null || rows.size() == 0) {
            return 0;
        }

        TableType table = prepareTable(tableName);
        try (PreparedStatement ps = getDatabase()
                .getConnection()
                .prepareStatement(table.generateDeleteSQL())) {
            for (Map<String, Object> row : rows) {
                int i = 1;
                for (ColumnType ct : table.getColumns()) {
                    if (!ct.isPk()) {
                        continue;
                    }

                    Object value = row.get(ct.getColumnName().toUpperCase());
                    ps.setObject(i, ct.read(getDatabase().getConnection(), value));
                    i++;
                }
                ps.addBatch();
            }

            return ps.executeBatch().length;
        }
    }

    @Override
    public synchronized void truncate(String tableName) throws SQLException {
        try (PreparedStatement ps = getDatabase()
                .getConnection()
                .prepareStatement("delete from " + tableName)) {
            ps.executeUpdate();
        }
    }

    @Override
    public synchronized TableType prepareTable(String tableName) throws SQLException {
        TableType cacheTable = this.cacheTables.get(tableName);
        if (cacheTable != null) {
            return cacheTable;
        }

        cacheTable = getDatabase().selectTable(tableName, false);
        if (cacheTable == null) {
            throw new SQLException(tableName + " not found");
        }
        this.cacheTables.put(tableName, cacheTable);

        AbstractTableType abstractTable = this.abstractTables.get(tableName);
        if (abstractTable != null) {
            List<ColumnType> cts = cacheTable.getColumns();
            List<String> pks = abstractTable.getPk().getColumn();
            cts.forEach(c -> {
                if (pks.contains(c.getColumnName())) {
                    c.setPk(true);
                }
            });
        }

        return cacheTable;
    }

    @Override
    public void beginTx() throws SQLException {
        getDatabase().getConnection().setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        getDatabase().getConnection().commit();
    }

	@Override
	public Savepoint createSavePoint(String name) throws SQLException {
		return getDatabase().getConnection().setSavepoint(name);
	}

    @Override
    public void rollback() throws SQLException {
        getDatabase().getConnection().rollback();
    }
    
    @Override
    public String toString() {
    	return String.format("%s::%s:%s/%s", 
    			this.databaseType.getId(),
    			this.databaseType.getHost(),
    			this.databaseType.getPort(),
    			this.databaseType.getDbName());
    }

    protected abstract Database getDatabase();

    /**
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
    */
}
