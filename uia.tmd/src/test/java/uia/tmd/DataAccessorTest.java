package uia.tmd;

import java.io.File;

import org.junit.Test;

import uia.tmd.model.TmdTypeHelper;
import uia.tmd.model.TmdTypeHelperTest;
import uia.tmd.model.xml.SourceSelectType;
import uia.tmd.model.xml.TargetUpdateType;
import uia.tmd.model.xml.TmdType;

public class DataAccessorTest {

    @Test
    public void testSQL() throws Exception {
        TmdType tmd = TmdTypeHelper.load(new File(TmdTypeHelperTest.class.getResource("sample.xml").toURI()));
        SourceSelectType st = tmd.getTaskSpace().getTask().get(0).getSourceSelect();
        TargetUpdateType tt = tmd.getTaskSpace().getTask().get(0).getTargetUpdate();
        System.out.println(DataAccessor.sqlSelect(st.getTable(), st.getColumns().getColumn(), st.getWhere().getColumn()));
        System.out.println(DataAccessor.sqlDelete(tt.getTable(), tt.getWhere().getColumn()));
        System.out.println(DataAccessor.sqlInsert(tt.getTable(), st.getColumns().getColumn()));
    }
}
