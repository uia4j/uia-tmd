package uia.tmd.access;

import uia.tmd.AbstractDataAccess;
import uia.utils.dao.Database;
import uia.utils.dao.pg.PostgreSQL;

/**
 * PostgreSQL data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PGSQLAccess extends AbstractDataAccess {

    private Database database;

    public PGSQLAccess() throws Exception {
    }

    @Override
    public void connect() throws Exception {
        this.database = new PostgreSQL(
                this.databaseType.getHost(),
                "" + this.databaseType.getPort(),
                this.databaseType.getDbName(),
                this.databaseType.getUser(),
                this.databaseType.getPassword());
    }

    @Override
    public void disconnect() throws Exception {
        try {
            this.database.close();
        }
        finally {
            this.database = null;
        }
    }

    @Override
    protected Database getDatabase() {
        return this.database;
    }
}
