package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.ExecJob;

public class ExecJobDao {

    private static final String SQL_INS = "INSERT INTO zzt_exec_job(id,tmd_job_bo,database_source,database_target,tmd_task_log_bo,executed_date,executed_time,executed_result,run_state,delete_after) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_UPD = "UPDATE zzt_exec_job SET tmd_job_bo=?,database_source=?,database_target=?,tmd_task_log_bo=?,executed_date=?,executed_time=?,executed_result=?,run_state=?,delete_after=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tmd_job_bo,database_source,database_target,tmd_task_log_bo,executed_date,executed_time,executed_result,run_state,delete_after FROM zzt_exec_job ";

    private final Connection conn;

    public ExecJobDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(ExecJob data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setString(2, data.getTmdJobBo());
            ps.setString(3, data.getDatabaseSource());
            ps.setString(4, data.getDatabaseTarget());
            ps.setString(5, data.getTmdTaskLogBo());
            ps.setTimestamp(6, new Timestamp(data.getExecutedDate().getTime()));
            ps.setTimestamp(7, new Timestamp(data.getExecutedTime().getTime()));
            ps.setString(8, data.getExecutedResult());
            ps.setString(9, data.getRunState());
            if (data.getDeleteAfter() == null) {
                ps.setNull(10, java.sql.Types.DATE);
            }
            else {
                ps.setTimestamp(10, new Timestamp(data.getDeleteAfter().getTime()));
            }

            return ps.executeUpdate();
        }
    }

    public int insert(List<ExecJob> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (ExecJob data : dataList) {
                ps.setString(1, data.getId());
                ps.setString(2, data.getTmdJobBo());
                ps.setString(3, data.getDatabaseSource());
                ps.setString(4, data.getDatabaseTarget());
                ps.setString(5, data.getTmdTaskLogBo());
                ps.setTimestamp(6, new Timestamp(data.getExecutedDate().getTime()));
                ps.setTimestamp(7, new Timestamp(data.getExecutedTime().getTime()));
                ps.setString(8, data.getExecutedResult());
                ps.setString(9, data.getRunState());
                if (data.getDeleteAfter() == null) {
                    ps.setNull(10, java.sql.Types.DATE);
                }
                else {
                    ps.setTimestamp(10, new Timestamp(data.getDeleteAfter().getTime()));
                }

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(ExecJob data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setString(1, data.getTmdJobBo());
            ps.setString(2, data.getDatabaseSource());
            ps.setString(3, data.getDatabaseTarget());
            ps.setString(4, data.getTmdTaskLogBo());
            ps.setTimestamp(5, new Timestamp(data.getExecutedDate().getTime()));
            ps.setTimestamp(6, new Timestamp(data.getExecutedTime().getTime()));
            ps.setString(7, data.getExecutedResult());
            ps.setString(8, data.getRunState());
            if (data.getDeleteAfter() == null) {
                ps.setNull(9, java.sql.Types.DATE);
            }
            else {
                ps.setTimestamp(9, new Timestamp(data.getDeleteAfter().getTime()));
            }
            ps.setString(10, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<ExecJob> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (ExecJob data : dataList) {
                ps.setString(1, data.getTmdJobBo());
                ps.setString(2, data.getDatabaseSource());
                ps.setString(3, data.getDatabaseTarget());
                ps.setString(4, data.getTmdTaskLogBo());
                ps.setTimestamp(5, new Timestamp(data.getExecutedDate().getTime()));
                ps.setTimestamp(6, new Timestamp(data.getExecutedTime().getTime()));
                ps.setString(7, data.getExecutedResult());
                ps.setString(8, data.getRunState());
                if (data.getDeleteAfter() == null) {
                    ps.setNull(9, java.sql.Types.DATE);
                }
                else {
                    ps.setTimestamp(9, new Timestamp(data.getDeleteAfter().getTime()));
                }
                ps.setString(10, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM zzt_exec_job WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public ExecJob selectByPK(String id) throws SQLException {
        ExecJob result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<ExecJob> selectAll() throws SQLException {
        ArrayList<ExecJob> result = new ArrayList<ExecJob>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private ExecJob convert(ResultSet rs) throws SQLException {
        ExecJob data = new ExecJob();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTmdJobBo(rs.getString(index++));
        data.setDatabaseSource(rs.getString(index++));
        data.setDatabaseTarget(rs.getString(index++));
        data.setTmdTaskLogBo(rs.getString(index++));
        data.setExecutedDate(rs.getTimestamp(index++));
        data.setExecutedTime(rs.getTimestamp(index++));
        data.setExecutedResult(rs.getString(index++));
        data.setRunState(rs.getString(index++));
        data.setDeleteAfter(rs.getTimestamp(index++));

        return data;
    }
}