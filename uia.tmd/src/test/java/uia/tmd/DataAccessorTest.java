package uia.tmd;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import uia.tmd.model.xml.ColumnType;

public class DataAccessorTest {

    @Test
    public void testSQL() throws Exception {
        System.out.println(DataAccessor.sqlSelect("human", prepareColumns(), prepareWhere()));
        System.out.println(DataAccessor.sqlInsert("human", prepareColumns()));
        System.out.println(DataAccessor.sqlDelete("human", prepareColumns()));
    }

    private List<ColumnType> prepareColumns() {
        ArrayList<ColumnType> cts = new ArrayList<ColumnType>();

        ColumnType f1 = new ColumnType();
        f1.setValue("id");
        f1.setPk(true);
        cts.add(f1);

        ColumnType f2 = new ColumnType();
        f2.setValue("first_name");
        f2.setPk(false);
        cts.add(f2);

        return cts;
    }

    private List<ColumnType> prepareWhere() {
        ArrayList<ColumnType> cts = new ArrayList<ColumnType>();

        ColumnType f1 = new ColumnType();
        f1.setValue("id");
        f1.setPk(true);
        cts.add(f1);

        return cts;
    }
}
