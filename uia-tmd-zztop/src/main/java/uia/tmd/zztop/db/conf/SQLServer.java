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
public class SQLServer {

    /**
     * Connection string.
     */
    private static String sqlserverConn;

    /**
     * User id.
     */
    private static String sqlserverUser;

    /**
     * Password.
     */
    private static String sqlserverPwd;

    /**
     * Schema
     */
    private static String sqlserverSchema;

    static {
        try {
        	DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        }
        catch (Exception ex) {

        }

        if (System.getProperties().get("tmd.zztop.db.connection") == null) {
            sqlserverConn = "jdbc:sqlserver://192.168.137.237:1433;databaseName=WIP;schema=dbo";
            sqlserverUser = "WIP";
            sqlserverPwd = "WIP";
            sqlserverSchema = "dbo";
        }
        else {
            sqlserverConn = "" + System.getProperty("tmd.zztop.db.connection");
            sqlserverUser = "" + System.getProperty("tmd.zztop.db.user");
            sqlserverPwd = "" + System.getProperty("tmd.zztop.db.pwd");
            sqlserverSchema = "" + System.getProperty("tmd.zztop.db.schema", "dbo");
        }
    }

    public static void config(String conn, String user, String pwd, String schema) {
        sqlserverConn = conn;
        sqlserverUser = user;
        sqlserverPwd = pwd;
        sqlserverSchema = schema;
    }

    public static String test() {
        return String.format("%s, user:%s, schema:%s", sqlserverConn, sqlserverUser, sqlserverSchema);
    }

    public static Connection create() throws SQLException {
        Connection conn = java.sql.DriverManager.getConnection(sqlserverConn, sqlserverUser, sqlserverPwd);
        conn.setSchema(sqlserverSchema);
        return conn;
    }
}