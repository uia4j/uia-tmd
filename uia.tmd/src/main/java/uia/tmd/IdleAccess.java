package uia.tmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.AbstractTableType;
import uia.tmd.model.xml.DatabaseType;
import uia.dao.ColumnDiff;
import uia.dao.ColumnType;
import uia.dao.Database;
import uia.dao.TableType;

/**
 * Idle data access.
 *
 * @author Kyle K. Lin
 *
 */
public class IdleAccess implements DataAccess, Database {

    @Override
    public void connect() throws SQLException {
    }

    @Override
    public void disconnect() throws SQLException {
    }

    @Override
    public List<Map<String, Object>> select(String talebName, String where) throws SQLException {
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> select(String talebName, String where, List<Object> params) throws SQLException {
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public int insert(String talebName, List<Map<String, Object>> rows) throws SQLException {
        return rows.size();
    }

    @Override
    public int delete(String tableName, List<Map<String, Object>> rows) throws SQLException {
        return rows.size();
    }

    @Override
    public void truncate(String tableName) throws SQLException {
    }

    @Override
    public TableType prepareTable(String tableName) throws SQLException {
        return null;
    }

    @Override
    public void initial(DatabaseType databaseType, Map<String, AbstractTableType> tables) {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public void setSchema(String schema) {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public List<String> selectTableNames() throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<String> selectTableNames(String prefix) throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<String> selectViewNames() throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<String> selectViewNames(String prefix) throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public boolean exists(String tableOrView) throws SQLException {
        return false;
    }

    @Override
    public TableType selectTable(String tableOrView, boolean firstAsPK) throws SQLException {
        return null;
    }

    @Override
    public List<ColumnType> selectColumns(String tableName, boolean firstAsPK) throws SQLException {
        return new ArrayList<ColumnType>();
    }

    @Override
    public String selectViewScript(String viewName) throws SQLException {
        return null;
    }

    @Override
    public String generateCreateTableSQL(TableType table) {
        return null;
    }

    @Override
    public int createTable(TableType table) throws SQLException {
        return 0;
    }

    @Override
    public int dropTable(String tableName) throws SQLException {
        return 0;
    }

    @Override
    public int createView(String viewName, String sql) throws SQLException {
        return 0;
    }

    @Override
    public int dropView(String viewName) throws SQLException {
        return 0;
    }

    @Override
    public int[] executeBatch(List<String> sqls) throws SQLException {
        return new int[sqls.size()];
    }

    @Override
    public int[] executeBatch(String sql, List<List<Object>> rows) throws SQLException {
        return new int[rows.size()];
    }

	@Override
	public void setAlwaysNVarchar(boolean alwaysNVarchar) {
	}

	@Override
	public boolean isAlwaysNVarchar() {
		return true;
	}

	@Override
	public void setAlwaysTimestampZ(boolean timestampZ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAlwaysTimestampZ() {
		return true;
	}

	@Override
	public String generateCreateViewSQL(String viewName, String sql) {
		return null;
	}

	@Override
	public String generateAlterTableSQL(String tableName, List<ColumnDiff> details) {
		return null;
	}

	@Override
	public String generateDropTableSQL(String tableName) {
		return null;
	}

	@Override
	public String generateDropViewSQL(String viewName) {
		return null;
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return true;
	}

    @Override
    public void beginTx() {
    }

    @Override
    public void commit() {
    }

	@Override
	public Savepoint createSavePoint(String name) throws SQLException {
		return null;
	}

    @Override
    public void rollback() {
    }
    
    @Override
    public String toString() {
    	return "Nowhere";
    }

	@Override
	public List<Object[]> query(String sql) throws SQLException {
		return new ArrayList<>();
	}
}
