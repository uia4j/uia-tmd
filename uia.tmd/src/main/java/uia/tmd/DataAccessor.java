package uia.tmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.TableType;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public interface DataAccessor {

    public void initial(DbServerType svrType, Map<String, TableType> tables);

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
    public abstract void connect() throws SQLException;

    /**
     * Disconnect to database.
     * @throws SQLException SQL exception.
     */
    public abstract void disconnect() throws SQLException;

    /**
     * Switch to manual commit. Use commit() to commit statements.
     * @throws SQLException SQL exception.
     */
    public void beingTx() throws SQLException;

    /**
     * Switch to auto commit.
     * @throws SQLException SQL exception.
     */
    public void endTx() throws SQLException;

    /**
     * Commit.
     * @throws SQLException SQL exception.
     */
    public void commit() throws SQLException;

    /**
     * Rollback.
     * @throws SQLException SQL exception.
     */
    public void rollback() throws SQLException;

    public List<String> listTables() throws SQLException;

    /**
     * Select data.
     * @param sql SQL statement.
     * @param wheres Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String sql, Where[] wheres) throws SQLException;

    /**
     * Select data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String sql, Map<String, Object> parameters) throws SQLException;

    /**
     * Insert, update or delete data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Count of updated records.
     * @throws SQLException SQL exception.
     */
    public int execueUpdate(String sql, Map<String, Object> parameters) throws SQLException;

    /**
     * Insert, update or delete data.
     * @param sql SQL statement.
     * @param table Values of parameters with ordering.
     * @throws SQLException SQL exception.
     */
    public int execueUpdateBatch(String sql, List<Map<String, Object>> table) throws SQLException;

    /**
     * Prepare column information of specific table.
     * @param tableName Table name.
     * @return Column information.
     * @throws SQLException Prepare failed.
     */
    public List<ColumnType> prepareColumns(String tableName) throws SQLException;

}
