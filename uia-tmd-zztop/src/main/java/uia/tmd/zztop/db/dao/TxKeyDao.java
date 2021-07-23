package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import uia.dao.DaoException;
import uia.dao.DaoMethod;
import uia.dao.TableDao;
import uia.dao.TableDaoHelper;
import uia.dao.annotation.DaoInfo;
import uia.dao.annotation.SelectInfo;
import uia.tmd.zztop.db.TxKey;

@DaoInfo(type = TxKey.class)
public abstract class TxKeyDao extends TableDao<TxKey> {

    public TxKeyDao(Connection conn, TableDaoHelper<TxKey> tableHelper) {
    	super(conn, tableHelper);
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

    @SelectInfo(sql = "WHERE table_name=?")
    public abstract List<TxKey> selectByTable(String tableName);
}
