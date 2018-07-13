package uia.tmd.access;

import uia.tmd.AbstractDataAccess;
import uia.utils.dao.Database;

/**
 * MS SQL Server data access.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccess extends AbstractDataAccess {

    // private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private Database database;

    public MSSQLAccess() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public void connect() throws Exception {
        /**
        DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getPort(), this.svrType.getDbName()),
                this.svrType.getUser(),
                this.svrType.getPassword());
        */
        this.database = null;
    }

    @Override
    public void disconnect() throws Exception {
    }

    @Override
    protected Database getDatabase() {
        return this.database;
    }
}
