package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import uia.dao.DaoException;
import uia.dao.DaoMethod;
import uia.dao.TableDao;
import uia.tmd.zztop.db.TxTable;
import uia.tmd.zztop.db.conf.TmdDB;

public class TxTableDao extends TableDao<TxTable> {

    public TxTableDao(Connection conn) {
    	super(conn, TmdDB.forTable(TxTable.class));
    }        
    
    public int count() throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("select count(*) from zzt_tx_table")) {
        	ResultSet rs = ps.executeQuery();
        	rs.next();
        	return rs.getInt(1);
        }
    }

    public int deleteByJob(String execJobBo) throws SQLException, DaoException {
    	DaoMethod<TxTable> method = this.tableHelper.forDelete();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE exec_job_bo=?")) {
            ps.setString(1, execJobBo);
            return ps.executeUpdate();
        }
    }

    public TxTable selectLastByTable(String tableName) throws SQLException, DaoException {
    	DaoMethod<TxTable> method = this.tableHelper.forSelect();
        try (PreparedStatement ps = this.conn.prepareStatement(method.getSql() + "WHERE table_name=? ORDER BY id desc")) {
            ps.setString(1, tableName);

            ResultSet rs = ps.executeQuery();
            return method.toOne(rs);
        }
    }
}
