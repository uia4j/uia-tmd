package uia.tmd.zztop.db.conf;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * HANA helper.
 *
 * @author Kyle K. Lin
 *
 */
public class AppSource {

    /**
     * Connection string.
     */
    private static String sourceName;

    static {
        if (System.getProperties().get("tmd.zztop.db.sourceName") == null) {
            sourceName = "jdbc/jts/wipPool";
        }
        else {
            sourceName = "" + System.getProperties().get("tmd.zztop.db.sourceName");
        }
    }

    private AppSource() {
    }

    public static void config(String sn) {
        sourceName = sn;
    }

    public static String test() {
        return "dataSource:" + sourceName;
    }

    public static Connection create() throws SQLException {
        try {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup(sourceName);

            return dataSource.getConnection();
        }
        catch (NamingException ex) {
            throw new SQLException("DataSource failed", ex);
        }
    }
}