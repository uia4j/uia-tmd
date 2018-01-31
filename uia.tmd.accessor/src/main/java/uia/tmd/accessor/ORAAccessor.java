package uia.tmd.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uia.tmd.AbstractDataAccessor;

import oracle.jdbc.driver.OracleConnection;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class ORAAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:oracle:thin:@%s:%s:%s";

    private OracleConnection conn;

    public ORAAccessor() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect() throws SQLException {
        this.conn = (OracleConnection) DriverManager.getConnection(
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
