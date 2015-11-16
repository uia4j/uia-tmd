package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PostgreSQL data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PGSQLAccessor extends DataAccessor {

    private static final String CONN = "jdbc:postgresql://%s:%s/%s";

    private final String connString;

    private Connection conn;

    /**
     * Constructor.
     * @param host Host name or ip.
     * @param port Port number.
     * @param dbName Database name.
     * @throws Exception Initial failure.
     */
    public PGSQLAccessor(String host, int port, String dbName) throws Exception {
        this.connString = String.format(CONN, host, port, dbName);
        System.out.println(this.connString);
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect(String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(this.connString, user, password);
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
