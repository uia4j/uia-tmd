package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import uia.tmd.model.xml.TableType;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PIDBAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:pisql://%s/Data Source=%s;Integrated Security=SSPI;";

    private final String connString;

    private Connection conn;

    PIDBAccessor(Map<String, TableType> tables, String host, int port, String dbName) throws Exception {
        super(tables);
        this.connString = String.format(CONN, host, dbName);
        System.out.println(this.connString);
        Class.forName("com.osisoft.jdbc.Driver").newInstance();
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect(String user, String password) throws SQLException {
        Properties plist = new Properties();
        plist.put("DCA", "SAVE");
        plist.put("user", user);
        plist.put("password", password);
        this.conn = DriverManager.getConnection(this.connString, plist);
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
