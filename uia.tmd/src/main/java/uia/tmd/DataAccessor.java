package uia.tmd;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Database accessor.
 *
 * @author Kyle K. Lin
 *
 */
public interface DataAccessor {

    /**
     * Connect to database.
     * @param user User id.
     * @param password Password.
     * @throws SQLException SQL exception.
     */
    public void connect(String user, String password) throws SQLException;

    /**
     * Select data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Result. Order of keys is same as selected columns.
     * @throws SQLException SQL exception.
     */
    public List<Map<String, Object>> select(String sql, List<Object> parameters) throws SQLException;

    /**
     * Insert, update or delete data.
     * @param sql SQL statement.
     * @param parameters Values of parameters with ordering.
     * @return Count of updated recored.
     * @throws SQLException SQL exception.
     */
    public int execueUpdate(String sql, List<Object> parameters) throws SQLException;

    /**
     * Disconnect to database.
     * @throws SQLException SQL exception.
     */
    public void disconnect() throws SQLException;
}
