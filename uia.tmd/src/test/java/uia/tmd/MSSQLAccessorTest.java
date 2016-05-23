package uia.tmd;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.TableType;

public class MSSQLAccessorTest {

    @Test
    public void testListTables() throws Exception {
        MSSQLAccessor accessor = new MSSQLAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "wip");
        accessor.connect("wip", "wip");
        List<String> data = accessor.listTables();
        accessor.disconnect();

        for (String t : data) {
            System.out.println(t);
        }
    }

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
    public void testInsert() throws Exception {

        MSSQLAccessor accessor = new MSSQLAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "mydb");
        accessor.connect("sa", "sqlAdm");

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", 0);
        data.put("first_name", "kyle");
        data.put("birthday", "2012-03-04");
        int i0 = 400000;

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 120000; i++) {
            data.put("id", i0 + i);
            accessor.execueUpdate("INSERT INTO human(id,first_name,birthday) VALUES(?,?,?)", data);
        }
        long t2 = System.currentTimeMillis();
        System.out.println(t2 - t1);

        accessor.disconnect();
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
