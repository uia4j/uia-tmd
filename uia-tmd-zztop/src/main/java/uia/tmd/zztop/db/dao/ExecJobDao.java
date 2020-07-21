package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uia.dao.TableDao;
import uia.tmd.zztop.db.ExecJob;
import uia.tmd.zztop.db.conf.TmdDB;

public class ExecJobDao extends TableDao<ExecJob> {

    public ExecJobDao(Connection conn) {
    	super(conn, TmdDB.forTable(ExecJob.class));
    }
    
    public int count() throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("select count(*) from zzt_exec_job")) {
        	ResultSet rs = ps.executeQuery();
        	rs.next();
        	return rs.getInt(1);
        }
    }
} 