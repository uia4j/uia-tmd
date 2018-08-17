package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class ExecutorLogDaoTest extends AbstractDao {

    @Test
    public void test() throws SQLException {
        try (Connection conn = createConn()) {
            ExecJobDao dao = new ExecJobDao(conn);
            dao.selectAll().forEach(System.out::println);

        }
    }
}
