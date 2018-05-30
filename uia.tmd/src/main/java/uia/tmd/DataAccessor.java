package uia.tmd;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.MColumnType;
import uia.tmd.model.xml.MTableType;
import uia.utils.dao.Database;
import uia.utils.dao.where.Where;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public interface DataAccessor {

    public void initial(DbServerType svrType, Map<String, MTableType> tables);

    /**
     * Get connection.
     * @return Connection.
     */
    public abstract Database getDatabase();

    /**
     * Connect to database.
     * @param user User id.
     * @param password Password.
     * @throws SQLException SQL exception.
     */
    public abstract void connect() throws Exception;

    /**
     * Disconnect to database.
     * @throws SQLException SQL exception.
     */
    public abstract void disconnect() throws Exception;

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
     * @param sql SQL statement without WHERE.
     * @param where WHERE statement.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String sql, Where where) throws SQLException;

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
    public int execueBatch(String sql, List<Map<String, Object>> table) throws SQLException;

    /**
     * Prepare column information of specific table.
     * @param tableName Table name.
     * @return Column information.
     * @throws SQLException Prepare failed.
     */
    public List<MColumnType> prepareColumns(String tableName) throws SQLException;

}
