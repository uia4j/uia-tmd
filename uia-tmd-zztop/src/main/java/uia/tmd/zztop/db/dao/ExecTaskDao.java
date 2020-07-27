package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uia.dao.DaoException;
import uia.dao.DaoMethod;
import uia.dao.TableDao;
import uia.tmd.zztop.db.ExecTask;
import uia.tmd.zztop.db.conf.TmdDB;

public class ExecTaskDao extends TableDao<ExecTask> {

    public ExecTaskDao(Connection conn) {
    	super(conn, TmdDB.forTable(ExecTask.class));
    }
    
    public int count() throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("select count(*) FROM zzt_exec_task")) {
        	try(ResultSet rs = ps.executeQuery()) {
            	rs.next();
            	return rs.getInt(1);
        	}
        }
    }
    
    public List<ExecTask> selectByJob(String execJobId) throws SQLException, DaoException {
    	DaoMethod<ExecTask> method = this.tableHelper.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE exec_job_bo=? ORDER BY group_id,task_path")) {
        	ps.setString(1, execJobId);
        	try(ResultSet rs = ps.executeQuery()) {
            	return method.toList(rs);
        	}
        }
    }
    
    public int deleteByJob(String execJobId) throws SQLException, DaoException {
    	DaoMethod<ExecTask> method = this.tableHelper.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE exec_job_bo=?")) {
        	ps.setString(1, execJobId);
        	return ps.executeUpdate();
        }
    }
}
