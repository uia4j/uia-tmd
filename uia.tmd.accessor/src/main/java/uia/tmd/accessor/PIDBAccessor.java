package uia.tmd.accessor;

import uia.tmd.AbstractDataAccessor;
import uia.utils.dao.Database;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PIDBAccessor extends AbstractDataAccessor {

    //private static final String CONN = "jdbc:pisql://%s/Data Source=%s;Integrated Security=SSPI;";

    public PIDBAccessor() throws Exception {
        //Class.forName("com.osisoft.jdbc.Driver").newInstance();
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    @Override
    public void connect() throws Exception {
        /**
        Properties plist = new Properties();
        plist.put("DCA", "SAVE");
        plist.put("user", this.svrType.getUser());
        plist.put("password", this.svrType.getPassword());

        this.conn = DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getDbName()),
                plist);
        */
    }

    @Override
    public void disconnect() throws Exception {
    }
}
