package uia.tmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.TableType;

/**
 * Idle data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class IdleAccessor implements DataAccessor {

    @Override
    public void connect() throws SQLException {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void disconnect() throws SQLException {
    }

    @Override
    public void beingTx() throws SQLException {

    }

    @Override
    public void endTx() throws SQLException {

    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public List<String> listTables() throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<Map<String, Object>> select(String sql, Where[] wheres) throws SQLException {
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public List<Map<String, Object>> select(String sql, Map<String, Object> parameters) throws SQLException {
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public int execueUpdate(String sql, Map<String, Object> parameters) throws SQLException {
        return 1;
    }

    @Override
    public int execueUpdateBatch(String sql, List<Map<String, Object>> table) throws SQLException {
        return 0;
    }

    @Override
    public List<ColumnType> prepareColumns(String tableName) throws SQLException {
        return new ArrayList<ColumnType>();
    }

    @Override
    public void initial(DbServerType svrType, Map<String, TableType> tables) {
    }
}
