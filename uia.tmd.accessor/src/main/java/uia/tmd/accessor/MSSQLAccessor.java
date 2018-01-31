package uia.tmd.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import uia.tmd.AbstractDataAccessor;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private SQLServerConnection conn;

    public MSSQLAccessor() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect() throws SQLException {
        this.conn = (SQLServerConnection) DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getPort(), this.svrType.getDbName()),
                this.svrType.getUser(),
                this.svrType.getPassword());
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
