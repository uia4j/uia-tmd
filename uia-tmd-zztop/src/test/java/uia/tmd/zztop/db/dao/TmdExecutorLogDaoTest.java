package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class TmdExecutorLogDaoTest extends AbstractDao {

    @Test
    public void test() throws SQLException {
        try (Connection conn = createConn()) {
            TmdExecutorLogDao dao = new TmdExecutorLogDao(conn);
            dao.selectAll().forEach(System.out::println);

        }
    }
}
