package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import uia.tmd.model.xml.TableType;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private final String connString;

    private SQLServerConnection conn;

    MSSQLAccessor(Map<String, TableType> tables, String host, int port, String dbName) throws Exception {
        super(tables);
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
