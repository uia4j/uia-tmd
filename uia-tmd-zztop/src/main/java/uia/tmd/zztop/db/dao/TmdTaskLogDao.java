package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.TmdTaskLog;

public class TmdTaskLogDao {

    private static final String SQL_INS = "INSERT INTO tmd_task_log(id,tmd_task_bo,tmd_executor_log_bo,table_name,sql_where,triggered_by,result_count,task_path) VALUES (?,?,?,?,?,?,?,?)";

    private static final String SQL_UPD = "UPDATE tmd_task_log SET tmd_task_bo=?,tmd_executor_log_bo=?,table_name=?,sql_where=?,triggered_by=?,result_count=?,task_path=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tmd_task_bo,tmd_executor_log_bo,table_name,sql_where,triggered_by,result_count,task_path FROM tmd_task_log ";

    private final Connection conn;

    public TmdTaskLogDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(TmdTaskLog data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setString(2, data.getTmdTaskBo());
            ps.setString(3, data.getTmdExecutorLogBo());
            ps.setString(4, data.getTableName());
            ps.setString(5, data.getSqlWhere());
            ps.setString(6, data.getTriggeredBy());
            ps.setInt(7, data.getResultCount());
            ps.setString(8, data.getTaskPath());

            return ps.executeUpdate();
        }
    }

    public int insert(List<TmdTaskLog> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (TmdTaskLog data : dataList) {
                ps.setString(1, data.getId());
                ps.setString(2, data.getTmdTaskBo());
                ps.setString(3, data.getTmdExecutorLogBo());
                ps.setString(4, data.getTableName());
                ps.setString(5, data.getSqlWhere());
                ps.setString(6, data.getTriggeredBy());
                ps.setInt(7, data.getResultCount());
                ps.setString(8, data.getTaskPath());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(TmdTaskLog data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setString(1, data.getTmdTaskBo());
            ps.setString(2, data.getTmdExecutorLogBo());
            ps.setString(3, data.getTableName());
            ps.setString(4, data.getSqlWhere());
            ps.setString(5, data.getTriggeredBy());
            ps.setInt(6, data.getResultCount());
            ps.setString(7, data.getTaskPath());
            ps.setString(8, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<TmdTaskLog> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (TmdTaskLog data : dataList) {
                ps.setString(1, data.getTmdTaskBo());
                ps.setString(2, data.getTmdExecutorLogBo());
                ps.setString(3, data.getTableName());
                ps.setString(4, data.getSqlWhere());
                ps.setString(5, data.getTriggeredBy());
                ps.setInt(6, data.getResultCount());
                ps.setString(7, data.getTaskPath());
                ps.setString(8, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM tmd_task_log WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public TmdTaskLog selectByPK(String id) throws SQLException {
        TmdTaskLog result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<TmdTaskLog> selectAll() throws SQLException {
        ArrayList<TmdTaskLog> result = new ArrayList<TmdTaskLog>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private TmdTaskLog convert(ResultSet rs) throws SQLException {
        TmdTaskLog data = new TmdTaskLog();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTmdTaskBo(rs.getString(index++));
        data.setTmdExecutorLogBo(rs.getString(index++));
        data.setTableName(rs.getString(index++));
        data.setSqlWhere(rs.getString(index++));
        data.setTriggeredBy(rs.getString(index++));
        data.setResultCount(rs.getInt(index++));
        data.setTaskPath(rs.getString(index++));

        return data;
    }
}
