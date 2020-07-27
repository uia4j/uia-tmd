package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractDao {

    protected Connection createConn() throws Exception {
        // return DriverManager.getConnection("jdbc:postgresql://localhost:5432/tmd", "postgres", "pgAdmin");

    	Class.forName("com.sap.db.jdbc.Driver");
    	Connection conn = DriverManager.getConnection("jdbc:sap://10.160.2.23:31015", "WIP_ARCHIVE", "Sap54321");
    	conn.setSchema("WIP_ARCHIVE");
    	System.out.println(conn.getSchema());
    	return conn;
    }
}
