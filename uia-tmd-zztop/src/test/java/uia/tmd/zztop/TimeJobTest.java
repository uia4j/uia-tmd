package uia.tmd.zztop;

import org.junit.Test;

import ui.tmd.zztop.TimeJob;

public class TimeJobTest {

    @Test
    public void test() throws Exception {
        TimeJob job = new TimeJob();
        job.run("MVSDB_ALL_IDLE", null);
    }
}
