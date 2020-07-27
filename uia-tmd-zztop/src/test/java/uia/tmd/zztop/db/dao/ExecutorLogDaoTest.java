package uia.tmd.zztop.db.dao;

import java.sql.Connection;

import org.junit.Test;

public class ExecutorLogDaoTest extends AbstractDao {

    @Test
    public void test() throws Exception {
        try (Connection conn = createConn()) {
            ExecJobDao dao = new ExecJobDao(conn);
            dao.selectAll().forEach(System.out::println);

        }
    }

    @Test
    public void testCount() throws Exception {
        try (Connection conn = createConn()) {
        	System.out.println(new ExecJobDao(conn).count());
        	System.out.println(new ExecTaskDao(conn).count());
        	System.out.println(new TxKeyDao(conn).count());
        	System.out.println(new TxTableDao(conn).count());

        }
    }
}
