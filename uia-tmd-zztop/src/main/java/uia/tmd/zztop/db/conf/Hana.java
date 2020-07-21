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
public class Hana {

    /**
     * Connection string.
     */
    private static String hanaConn;

    /**
     * User id.
     */
    private static String hanaUser;

    /**
     * Password.
     */
    private static String hanaPwd;

    /**
     * Schema.
     */
    private static String hanaSchema;

    static {
        try {
        	DriverManager.registerDriver(new com.sap.db.jdbc.Driver());
        }
        catch (Exception e) {

        }

        if (System.getProperties().get("tmd.zztop.db.connection") == null) {
            hanaConn = "jdbc:sap://192.168.137.245:39015";
            hanaUser = "WIP";
            hanaPwd = "Sap12345";
        }
        else {
            hanaConn = "" + System.getProperties().get("tmd.zztop.db.connection");
            hanaUser = "" + System.getProperties().get("tmd.zztop.db.user");
            hanaPwd = "" + System.getProperties().get("tmd.zztop.db.pwd");
        }
    }

    private Hana() {
    }

    public static void config(String conn, String user, String pwd, String schema) {
        hanaConn = conn;
        hanaUser = user;
        hanaPwd = pwd;
        hanaSchema = schema;
    }

    public static String test() {
        return String.format("%s, user:%s, schema:%s", hanaConn, hanaUser, hanaSchema);
    }

    public static Connection create() throws SQLException {
        return java.sql.DriverManager.getConnection(hanaConn, hanaUser, hanaPwd);
    }
}