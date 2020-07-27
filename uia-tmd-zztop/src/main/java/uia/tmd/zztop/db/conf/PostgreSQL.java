package uia.tmd.zztop.db.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PostgreSQL helper.
 *
 * @author Kyle K. Lin
 *
 */
public class PostgreSQL {

    /**
     * Connection string.
     */
    private static String pgConn;

    /**
     * User id.
     */
    private static String pgUser;

    /**
     * Password.
     */
    private static String pgPwd;

    /**
     * Schema.
     */
    private static String pgSchema;

    static {
        try {
        	DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (Exception ex) {

        }

        if (System.getProperties().get("tmd.zztop.db.connection") == null) {
            pgConn = "jdbc:postgresql://localhost:5432/pmsdb";
            pgUser = "pms";
            pgPwd = "pms";
            pgSchema = "zztop";
        }
        else {
            pgConn = "" + System.getProperty("tmd.zztop.db.connection");
            pgUser = "" + System.getProperty("tmd.zztop.db.user");
            pgPwd = "" + System.getProperty("tmd.zztop.db.pwd");
            pgSchema = "" + System.getProperty("tmd.zztop.db.schema", "zztop");
        }
    }

    private PostgreSQL() {
    }

    public static void config(String conn, String user, String pwd, String schema) {
        pgConn = conn;
        pgUser = user;
        pgPwd = pwd;
        pgSchema = schema;
    }

    public static String test() {
        return String.format("%s, user:%s, schema:%s", pgConn, pgUser, pgSchema);
    }

    public static Connection create() throws SQLException {
        Connection conn = DriverManager.getConnection(pgConn, pgUser, pgPwd);
        conn.setSchema(pgSchema);
        return conn;
    }
}
