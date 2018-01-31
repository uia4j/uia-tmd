package uia.tmd.accessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.Where;
import uia.tmd.model.xml.ColumnType;
import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.TableType;

public class MSSQLAccessorTest {

    @Test
    public void testListTables() throws Exception {
        ORAAccessor accessor = new ORAAccessor();
        accessor.initial(createDb(), new TreeMap<String, TableType>());
        accessor.connect();

        List<String> data = accessor.listTables();
        accessor.disconnect();

        for (String t : data) {
            System.out.println(t);
        }
    }

    @Test
    public void testSelect() throws Exception {
        ORAAccessor accessor = new ORAAccessor();
        accessor.initial(createDb(), new TreeMap<String, TableType>());
        accessor.connect();

        List<Map<String, Object>> data = accessor.select("SELECT * FROM SFC", new Where[0]);
        accessor.disconnect();

        for (Map<String, Object> row : data) {
            System.out.println(row);
        }
    }

    public void testInsert() throws Exception {
        ORAAccessor accessor = new ORAAccessor();
        accessor.initial(createDb(), new TreeMap<String, TableType>());
        accessor.connect();

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

    public void testSelectAndInsert() throws Exception {
        ORAAccessor accessor = new ORAAccessor();
        accessor.initial(createDb(), new TreeMap<String, TableType>());
        accessor.connect();

        // select
        List<Map<String, Object>> data = accessor.select("select * from human", new Where[0]);
        // insert
        for (Map<String, Object> row : data) {
            accessor.execueUpdate("INSERT INTO people(id,first_name,birthday) VALUES(?,?,?)", row);
        }

        accessor.disconnect();
    }

    @Test
    public void testPrepareColumns() throws Exception {
        ORAAccessor accessor = new ORAAccessor();
        accessor.initial(createDb(), new TreeMap<String, TableType>());
        accessor.connect();

        List<ColumnType> cts = accessor.prepareColumns("ZD_SFC");
        for (ColumnType ct : cts) {
            System.out.println(ct.getValue());
        }
        accessor.disconnect();
    }

    private DbServerType createDb() {
        DbServerType type = new DbServerType();
        type.setDbName("MESDEV");
        type.setHost("10.160.1.48");
        type.setPort(1521);
        type.setUser("WIP");
        type.setPassword("wip");
        return type;
    }
}
