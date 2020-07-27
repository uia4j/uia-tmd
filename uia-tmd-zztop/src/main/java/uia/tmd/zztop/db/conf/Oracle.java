package uia.tmd.zztop.db.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * HANA helper.
 *
 * @author Kyle K. Lin
 *
 */
public class Oracle {

    /**
     * Connection string.
     */
    private static String oraConn;

    /**
     * User id.
     */
    private static String oraUser;

    /**
     * Password.
     */
    private static String oraPwd;

    /**
     * Schema
     */
    private static String oraSchema;

    static {
        try {
        	DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        }
        catch (Exception ex) {
            System.out.println("oracle failed");
        }

        if (System.getProperties().get("tmd.zztop.db.connection") == null) {
            oraConn = "jdbc:oracle:thin:@//localhost:1521/orcl.localdomain";
            oraUser = "pms";
            oraPwd = "pms";
            oraSchema = null;
        }
        else {
            oraConn = "" + System.getProperty("tmd.zztop.db.connection");
            oraUser = "" + System.getProperty("tmd.zztop.db.user");
            oraPwd = "" + System.getProperty("tmd.zztop.db.pwd");
            oraSchema = null;
        }
    }

    private Oracle() {
    }

    public static void config(String conn, String user, String pwd, String schema) {
        oraConn = conn;
        oraUser = user;
        oraPwd = pwd;
        oraSchema = schema;
    }

    public static String test() {
        return String.format("%s, user:%s, schema:%s", oraConn, oraUser, oraSchema);
    }

    public static Connection create() throws SQLException {
        return java.sql.DriverManager.getConnection(oraConn, oraUser, oraPwd);
    }
}