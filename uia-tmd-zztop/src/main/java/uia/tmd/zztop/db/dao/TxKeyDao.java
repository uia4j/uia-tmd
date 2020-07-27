package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uia.dao.DaoException;
import uia.dao.DaoMethod;
import uia.dao.TableDao;
import uia.tmd.zztop.db.TxKey;
import uia.tmd.zztop.db.conf.TmdDB;

public class TxKeyDao extends TableDao<TxKey> {

    public TxKeyDao(Connection conn) {
    	super(conn, TmdDB.forTable(TxKey.class));
    }
    
    public int count() throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("select count(*) from zzt_tx_key")) {
        	ResultSet rs = ps.executeQuery();
        	rs.next();
        	return rs.getInt(1);
        }
    }

    public int delete(List<String> keys) throws SQLException {
    	if(keys.size() == 0) {
    		return 0;
    	}
    	
    	DaoMethod<TxKey> method = this.tableHelper.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE id=?")) {
            for (String key : keys) {
                ps.setString(1, key);
                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int deleteByJob(String execJobBo) throws SQLException, DaoException {
    	DaoMethod<TxKey> method = this.tableHelper.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE exec_job_bo=?")) {
            ps.setString(1, execJobBo);
            return ps.executeUpdate();
        }
    }

    public List<TxKey> selectByTable(String tableName) throws SQLException, DaoException {
    	DaoMethod<TxKey> method = this.tableHelper.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE table_name=?")) {
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            return method.toList(rs);
        }
    }
}
