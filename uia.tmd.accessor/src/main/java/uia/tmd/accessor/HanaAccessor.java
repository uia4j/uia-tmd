package uia.tmd.accessor;

import uia.tmd.AbstractDataAccessor;
import uia.utils.dao.Database;
import uia.utils.dao.hana.Hana;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class HanaAccessor extends AbstractDataAccessor {

    private Database database;

    public HanaAccessor() throws Exception {
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public void connect() throws Exception {
        this.database = new Hana(
                this.svrType.getHost(),
                "" + this.svrType.getPort(),
                this.svrType.getUser(),
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
