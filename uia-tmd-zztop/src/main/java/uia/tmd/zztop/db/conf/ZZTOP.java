package uia.tmd.zztop.db.conf;

import uia.dao.DaoEnv;
import uia.dao.DaoException;
import uia.dao.DaoFactory;

public class ZZTOP extends DaoEnv {

    private static ZZTOP masterDB;

    public synchronized static void initial() throws DaoException {
        if (masterDB == null) {
            masterDB = new ZZTOP(DaoEnv.HANA, false);
        }
        masterDB.config(
                "jdbc:sap://10.160.2.38:30015",
                "WIP",
                "Sap12345",
                "WIP");
    }

    public synchronized static void initial(String env, boolean dateToUTC, String conn, String user, String pwd, String schema) throws DaoException {
        if (masterDB == null) {
            if (env == null || env.isEmpty()) {
                initial();
            }
            else {
                masterDB = new ZZTOP(env, dateToUTC);
                masterDB.config(conn, user, pwd, schema);
            }
        }
    }

    public synchronized static DaoEnv env() throws DaoException {
        if (masterDB == null) {
            initial();
        }
        return masterDB;
    }

    private ZZTOP(String env, boolean dateToUTC) throws DaoException {
        super(env, dateToUTC);
    }

    @Override
    protected void initialFactory(DaoFactory factory) throws Exception {
        factory.load("uia.tmd.zztop.db");
    }

}
