package uia.tmd;

import java.util.TreeMap;

import org.junit.Test;

import uia.tmd.model.xml.TableType;

public class ORAAccessorTest {

    @Test
    public void test() throws Exception {
        new ORAAccessor(new TreeMap<String, TableType>(), "localhost", 1433, "wip");
    }
}
