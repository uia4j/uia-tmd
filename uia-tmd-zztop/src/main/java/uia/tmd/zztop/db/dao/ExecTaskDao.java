package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.ExecTask;

public class ExecTaskDao {

    private static final String SQL_INS = "INSERT INTO zzt_exec_task(id,tmd_task_bo,exec_job_bo,table_name,sql_where,triggered_by,result_count,task_path) VALUES (?,?,?,?,?,?,?,?)";

    private static final String SQL_UPD = "UPDATE zzt_exec_task SET tmd_task_bo=?,exec_job_bo=?,table_name=?,sql_where=?,triggered_by=?,result_count=?,task_path=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tmd_task_bo,exec_job_bo,table_name,sql_where,triggered_by,result_count,task_path FROM zzt_exec_task ";

    private final Connection conn;

    public ExecTaskDao(Connection conn) {
        this.conn = conn;
    }
    
    public int count() throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("select count(*) from zzt_exec_task")) {
        	ResultSet rs = ps.executeQuery();
        	rs.next();
        	return rs.getInt(1);
        }
    }

    public int insert(ExecTask data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setString(2, data.getTmdTaskBo());
            ps.setString(3, data.getExecJobBo());
            ps.setString(4, data.getTableName());
            ps.setString(5, data.getSqlWhere());
            ps.setString(6, data.getTriggeredBy());
            ps.setInt(7, data.getResultCount());
            ps.setString(8, data.getTaskPath());

            return ps.executeUpdate();
        }
    }

    public int insert(List<ExecTask> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (ExecTask data : dataList) {
                ps.setString(1, data.getId());
                ps.setString(2, data.getTmdTaskBo());
                ps.setString(3, data.getExecJobBo());
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

    public int update(ExecTask data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setString(1, data.getTmdTaskBo());
            ps.setString(2, data.getExecJobBo());
            ps.setString(3, data.getTableName());
            ps.setString(4, data.getSqlWhere());
            ps.setString(5, data.getTriggeredBy());
            ps.setInt(6, data.getResultCount());
            ps.setString(7, data.getTaskPath());
            ps.setString(8, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<ExecTask> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (ExecTask data : dataList) {
                ps.setString(1, data.getTmdTaskBo());
                ps.setString(2, data.getExecJobBo());
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
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM zzt_exec_task WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public ExecTask selectByPK(String id) throws SQLException {
        ExecTask result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<ExecTask> selectAll() throws SQLException {
        ArrayList<ExecTask> result = new ArrayList<ExecTask>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private ExecTask convert(ResultSet rs) throws SQLException {
        ExecTask data = new ExecTask();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTmdTaskBo(rs.getString(index++));
        data.setExecJobBo(rs.getString(index++));
        data.setTableName(rs.getString(index++));
        data.setSqlWhere(rs.getString(index++));
        data.setTriggeredBy(rs.getString(index++));
        data.setResultCount(rs.getInt(index++));
        data.setTaskPath(rs.getString(index++));

        return data;
    }
}
