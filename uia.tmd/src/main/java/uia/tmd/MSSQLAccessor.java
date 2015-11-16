package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccessor extends DataAccessor {

    private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private final String connString;

    private SQLServerConnection conn;

    /**
     * Constructor.
     * @param host Host name or ip.
     * @param port Port number.
     * @param dbName Database name.
     * @throws Exception Initial failure.
     */
    public MSSQLAccessor(String host, int port, String dbName) throws Exception {
        this.connString = String.format(CONN, host, port, dbName);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect(String user, String password) throws SQLException {
        this.conn = (SQLServerConnection) DriverManager.getConnection(this.connString, user, password);
    }

    @Override
    public void disconnect() throws SQLException {
        try {
            this.conn.close();
        }
        finally {
            this.conn = null;
        }
    }
}
