package uia.tmd.zztop;

import org.junit.Test;

public class ZztopCLITest {

	@Test
	public void testSync() throws Exception {
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mvs.xml", "-j", "Run" });
	}

	@Test
	public void testSyncWOF() throws Exception {
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mvs.xml", "--job", "RunWOF" });
	}
}
