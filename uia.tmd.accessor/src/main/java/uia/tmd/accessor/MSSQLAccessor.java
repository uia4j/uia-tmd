package uia.tmd.accessor;

import uia.tmd.AbstractDataAccessor;
import uia.utils.dao.Database;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccessor extends AbstractDataAccessor {

    private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private Database databaes;

    public MSSQLAccessor() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public Database getDatabase() {
        return null;
    }

    @Override
    public void connect() throws Exception {
        /**
        this.conn = (SQLServerConnection) DriverManager.getConnection(
                String.format(CONN, this.svrType.getHost(), this.svrType.getPort(), this.svrType.getDbName()),
                this.svrType.getUser(),
                this.svrType.getPassword());
        */
    }

    @Override
    public void disconnect() throws Exception {
    }
}
