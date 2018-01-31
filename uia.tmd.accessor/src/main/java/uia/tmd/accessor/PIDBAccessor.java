package uia.tmd.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import uia.tmd.AbstractDataAccessor;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PIDBAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:pisql://%s/Data Source=%s;Integrated Security=SSPI;";

    private Connection conn;

    public PIDBAccessor() throws Exception {
        Class.forName("com.osisoft.jdbc.Driver").newInstance();
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void connect() throws SQLException {
        Properties plist = new Properties();
        plist.put("DCA", "SAVE");
        plist.put("user", this.svrType.getUser());
        plist.put("password", this.svrType.getPassword());

        this.conn = DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getDbName()),
                plist);
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
