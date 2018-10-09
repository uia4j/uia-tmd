package uia.tmd.zztop.db.dao;

import org.junit.Test;

import uia.tmd.zztop.DB;
import uia.tmd.zztop.ZztopEnv;
import uia.tmd.zztop.db.QtzClock;

public class QtzClockDaoTest {

    @Test
    public void testInsert() throws Exception {
        ZztopEnv.initial();

        QtzClockDao dao = new QtzClockDao(DB.create());

        QtzClock qc = new QtzClock();
        qc.setTmdJobBo("SHOP_ORDER");
        qc.setClockType("min");
        dao.insert(qc);
    }
}
