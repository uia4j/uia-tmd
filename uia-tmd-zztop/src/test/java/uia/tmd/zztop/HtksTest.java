package uia.tmd.zztop;

import org.junit.Test;

public class HtksTest {

	@Test
	public void testSOMSync() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_month_sync",
				"-y", "2000",
				"-m", "1"
		});
	}

	@Test
	public void testSOMDelete() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_month_delete",
				"-y", "2019",
				"-m", "1"
		});
	}
}
