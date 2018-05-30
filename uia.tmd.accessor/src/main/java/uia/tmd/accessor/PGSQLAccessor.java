package uia.tmd.accessor;

import uia.tmd.AbstractDataAccessor;
import uia.utils.dao.Database;
import uia.utils.dao.pg.PostgreSQL;

/**
 * PostgreSQL data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class PGSQLAccessor extends AbstractDataAccessor {

    private Database database;

    public PGSQLAccessor() throws Exception {
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public void connect() throws Exception {
        this.database = new PostgreSQL(
                this.svrType.getHost(),
                "" + this.svrType.getPort(),
                this.svrType.getDbName(),
                this.svrType.getUser(),
                this.svrType.getPassword());
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
}
