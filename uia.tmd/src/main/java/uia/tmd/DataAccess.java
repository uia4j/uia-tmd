package uia.tmd;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.AbstractTableType;
import uia.tmd.model.xml.DatabaseType;
import uia.dao.TableType;

/**
 * Database access.
 *
 * @author Kyle K. Lin
 *
 */
public interface DataAccess {

    public void initial(DatabaseType databaseType, Map<String, AbstractTableType> tables);

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
     * Select data.
     * @param tableName Table name.
     * @param where WHERE statement with parameters.
     * @return Result.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String tableName, String where) throws SQLException;

    /**
     * Select data.
     * @param tableName Table name.
     * @param where WHERE statement with parameters.
     * @param params Values of parameters.
     * @return Result.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String tableName, String where, List<Object> params) throws SQLException;

    /**
     * Insert data.
     * @param tableName Table name.
     * @param rows Rows.
     * @return Count of updated records.
     * @throws SQLException SQL exception.
     */
    public int insert(String tableName, List<Map<String, Object>> rows) throws SQLException;

    /**
     * Delete data
     * @param tableName Table name.
     * @param rows Rows.
     * @return Count of updated records.
     * @throws SQLException SQL exception.
     */
    public int delete(String tableName, List<Map<String, Object>> rows) throws SQLException;

    /**
     * Truncate table.
     * @param tableName Table name.
     * @throws SQLException SQL exception.
     */
    public void truncate(String tableName) throws SQLException;

    /**
     * Prepare column information of specific table.
     * @param tableName Table name.
     * @return Column information.
     * @throws SQLException Prepare failed.
     */
    public TableType prepareTable(String tableName) throws SQLException;

    public void beginTx() throws SQLException;

    public void commit() throws SQLException;

    public Savepoint createSavePoint(String name) throws SQLException;

    public void rollback() throws SQLException;

}
