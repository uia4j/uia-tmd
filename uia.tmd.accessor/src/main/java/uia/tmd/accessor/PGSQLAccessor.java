package uia.tmd.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uia.tmd.AbstractDataAccessor;

/**
 * PostgreSQL data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PGSQLAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:postgresql://%s:%s/%s";

    private Connection conn;

    public PGSQLAccessor() throws Exception {
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect() throws SQLException {
        this.conn = DriverManager.getConnection(
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
