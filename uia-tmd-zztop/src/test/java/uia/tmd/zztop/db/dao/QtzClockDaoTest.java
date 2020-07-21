package uia.tmd.zztop.db.dao;

import org.junit.Test;

import uia.tmd.zztop.ZztopEnv;
import uia.tmd.zztop.db.QtzClock;
import uia.tmd.zztop.db.conf.TmdDB;

public class QtzClockDaoTest {

    @Test
    public void testInsert() throws Exception {
        ZztopEnv.initial();

        QtzClockDao dao = new QtzClockDao(TmdDB.create());

        QtzClock qc = new QtzClock();
        qc.setTmdJobBo("SHOP_ORDER");
        qc.setClockType("min");
        dao.insert(qc);
    }
}
