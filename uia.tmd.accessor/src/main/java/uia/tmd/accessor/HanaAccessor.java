package uia.tmd.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uia.tmd.AbstractDataAccessor;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class HanaAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:sap://%s:%s";

    private Connection conn;

    public HanaAccessor() throws Exception {
        Class.forName("com.sap.db.jdbc.Driver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect() throws SQLException {
        this.conn = DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getPort()),
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
