package uia.tmd.zztop;

import org.junit.Test;

public class ZztopCLITest {

	@Test
	public void testFailed() throws Exception {
		ZztopCLI.main(new String[] { "syn", "-f", "conf/mv.xml", "-j", "Run" });
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mv.xml", "-j", "Run" });
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mvs.xml", "-j", "Ru" });
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mv.xml", "-j", "Run", "-w" });
	}

	@Test
	public void testSync() throws Exception {
		//ZztopCLI.main(new String[] { "-c", "sync", "-f", "conf/mvs.xml", "-j", "Run" });
		ZztopCLI.main(new String[] { "-c", "sync", "-f", "conf/mvs.xml", "-j", "Run", "-w", "id='123'" });
	}

	@Test
	public void testSyncWOF() throws Exception {
		ZztopCLI.main(new String[] { "sync", "-f", "conf/mvs.xml", "--job", "RunWOF" });
	}
}
