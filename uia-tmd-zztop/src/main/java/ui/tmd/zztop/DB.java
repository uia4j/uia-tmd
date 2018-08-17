package ui.tmd.zztop;

import java.sql.Connection;
import java.sql.SQLException;

public class DB {

    public static String CONN;

    public static String USER;

    public static String PWD;

    static {
        CONN = "jdbc:postgresql://localhost:5432/tmd";
        USER = "postgres";
        PWD = "pgAdmin";

        if (System.getProperties().getProperty("zzt.db.driver") != null) {
            try {
                Class.forName(System.getProperties().getProperty("zzt.db.driver"));
                CONN = System.getProperties().getProperty("zzt.db.connection");
                USER = System.getProperties().getProperty("zzt.db.user");
                PWD = System.getProperties().getProperty("zzt.db.pwd");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection create() throws SQLException {
        return java.sql.DriverManager.getConnection(CONN, USER, PWD);
    }
}
