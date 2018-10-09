package uia.tmd.zztop;

import org.junit.Test;

import uia.tmd.zztop.DB;
import uia.tmd.zztop.ZztopEnv;

public class DBTest {

    @Test
    public void test1() throws Exception {
        System.out.println(DB.create());
    }

    @Test
    public void test2() throws Exception {
        ZztopEnv.initial();
        System.out.println(DB.create());
    }
}
