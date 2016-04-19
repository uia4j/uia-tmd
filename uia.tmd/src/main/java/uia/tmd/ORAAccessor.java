package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import oracle.jdbc.driver.OracleConnection;

import uia.tmd.model.xml.TableType;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class ORAAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:oracle:thin:@%s:%s::%s";

    private final String connString;

    private OracleConnection conn;

    ORAAccessor(Map<String, TableType> tables, String host, int port, String instance) throws Exception {
        super(tables);
        this.connString = String.format(CONN, host, port, instance);
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect(String user, String password) throws SQLException {
        this.conn = (OracleConnection) DriverManager.getConnection(this.connString, user, password);
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
