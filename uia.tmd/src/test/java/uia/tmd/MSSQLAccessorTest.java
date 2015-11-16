package uia.tmd;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class MSSQLAccessorTest {

    @Test
    public void testSelect() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor("localhost", 1433, "mydb");
        accessor.connect("sa", "sqlAdm");
        List<Map<String, Object>> data = accessor.select("select * from human", null);
        accessor.disconnect();

        for (Map<String, Object> row : data) {
            System.out.println(row);
        }
    }

    @Test
    public void testSelectAndInsert() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor("localhost", 1433, "mydb");
        accessor.connect("sa", "sqlAdm");

        // select
        List<Map<String, Object>> data = accessor.select("select * from human", null);
        // insert
        for (Map<String, Object> row : data) {
            accessor.execueUpdate("INSERT INTO people(id,first_name,birthday) VALUES(?,?,?)", row);
        }

        accessor.disconnect();
    }
}
