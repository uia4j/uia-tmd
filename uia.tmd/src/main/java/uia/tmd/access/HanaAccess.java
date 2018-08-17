package uia.tmd.access;

import uia.tmd.AbstractDataAccess;
import uia.utils.dao.Database;
import uia.utils.dao.hana.Hana;

public class HanaAccess extends AbstractDataAccess {

    private Database database;

    public HanaAccess() throws Exception {
    }

    @Override
    public void connect() throws Exception {
        this.database = new Hana(
                this.databaseType.getHost(),
                "" + this.databaseType.getPort(),
                this.databaseType.getUser(),
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
