package uia.tmd.zztop.db.dao;

import org.junit.Test;

import uia.dao.DaoSession;
import uia.tmd.zztop.db.conf.ZZTOP;

public class ExecutorLogDaoTest extends AbstractDao {

    public ExecutorLogDaoTest() throws Exception {
		super();
	}

	@Test
    public void testCount() throws Exception {
        try (DaoSession session = ZZTOP.env().createSession()) {
        	System.out.println(session.tableDao(ExecJobDao.class).count());
        	System.out.println(session.tableDao(ExecTaskDao.class).count());
        	System.out.println(session.tableDao(TxKeyDao.class).count());
        	System.out.println(session.tableDao(TxTableDao.class).count());
        }
    }
}
