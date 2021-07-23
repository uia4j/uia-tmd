package uia.tmd.zztop;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import uia.dao.DaoSession;
import uia.tmd.TaskFactory;
import uia.tmd.zztop.db.conf.ZZTOP;

public class HtksTest {

	public void testTruncate() throws URISyntaxException, Exception {
        TaskFactory factory = new TaskFactory(new File("HTKS.xml"));
        try(DaoSession session = ZZTOP.env().createSession()) {
            factory.getTasks().getTask().forEach(t -> {
            	try {
            		System.out.println(t.getName());
					session.conn.createStatement().execute("truncate table " + t.getName());
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
            });
			session.conn.createStatement().execute("truncate table zzt_exec_job");
			session.conn.createStatement().execute("truncate table zzt_exec_task");
			session.conn.createStatement().execute("truncate table zzt_tx_key");
			session.conn.createStatement().execute("truncate table zzt_tx_table");
        }
	}
	
	public void testSOSyncMonth() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_month",
				"-y", "2018",
				"-m", "5"
		});
	}

	public void testSODeleteMonth() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_delete_month",
				"-y", "2018",
				"-m", "04",
				"-j", "SO_DELETE_PROD"
		});
	}

	@Test
	public void testSOSyncDay() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_day",
				"-d", "2018/07/31"
		});
	}

	public void testSODeleteDay() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_delete_day",
				"-d", "2018/07/31",
				"-j", "SO_DELETE_DW"
		});
	}

	@Test
	public void testSOSyncOne() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_one",
				"-o", "ShopOrderBO:1020,BTC-210505270",
				"-j", "SO_SYNC_TEST"
		});
	}

	@Test
	public void testSODeleteOne() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_delete_one",
				"-o", "ShopOrderBO:1020,BSB-210525853",
				"-j", "SO_DELETE_TEST"
		});
	}

	public void testTestResult() throws Exception {
		ZztopCLI.main(new String[] { 
				"test_result",
				"-f", "test.txt"
		});
	}
}
