package uia.tmd;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.TableType;

public class MSSQLAccessorTest {

    @Test
    public void testSelect() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "mydb");
        accessor.connect("sa", "sqlAdm");
        List<Map<String, Object>> data = accessor.select("select * from human", (Map<String, Object>) null);
        accessor.disconnect();

        for (Map<String, Object> row : data) {
            System.out.println(row);
        }
    }

    @Test
    public void testSelectAndInsert() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "mydb");
        accessor.connect("sa", "sqlAdm");

        // select
        List<Map<String, Object>> data = accessor.select("select * from human", (Map<String, Object>) null);
        // insert
        for (Map<String, Object> row : data) {
            accessor.execueUpdate("INSERT INTO people(id,first_name,birthday) VALUES(?,?,?)", row);
        }

        accessor.disconnect();
    }

    @Test
    public void testPrepareColumns() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "pidb");
        accessor.connect("sa", "sqlAdm");
        List<ColumnType> cts = accessor.prepareColumns("picomp2");
        for (ColumnType ct : cts) {
            System.out.println(ct.getValue());
        }
        accessor.disconnect();
    }
}
