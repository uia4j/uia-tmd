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

	public void testSOSyncDay1() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_day",
				"-d", "2018/07/31"
		});
	}

	public void testSOSyncDay2() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_day",
				"-j", "SO_SYNC_TEST",
				"-o", "534"
		});
	}

	public void testSOSyncDay3() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_sync_day",
				"-d", "2018/12/31",
				"-j", "SO_SYNC_AND_DELETE"
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
				"-o", "ShopOrderBO:1020,BTX-210813226",
				"-j", "SO_SYNC_REV"
		});
	}

	public void testSODeleteOne() throws Exception {
		ZztopCLI.main(new String[] { 
				"so_delete_one",
				"-o", "ShopOrderBO:1020,TEST-0722110702",
				"-j", "SO_DELETE_TEST"
		});
	}

	public void testTestResult() throws Exception {
		ZztopCLI.main(new String[] { 
				"test_result",
				"-f", "test.txt"
		});
	}

	@Test
	public void testConfigLogSyncDay1() throws Exception {
		ZztopCLI.main(new String[] { 
				"cfglog_sync_day",
				"-d", "2022/01/01",
				"-j", "CONFIG_LOG_SYNC"
		});
	}
	
	public void testConfigLogSyncMonth() throws Exception {
		ZztopCLI.main(new String[] { 
				"cfglog_sync_month",
				"-y", "2021",
				"-m", "12",
				"-j", "CONFIG_LOG_SYNC_AND_DELETE"
		});
	}


	public void testZrFileDay1() throws Exception {
		ZztopCLI.main(new String[] { 
				"zr_file_day",
				"-d", "2021/01/29"
		});
	}
}
