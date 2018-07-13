package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.TmdExecutorLog;

public class TmdExecutorLogDao {

    private static final String SQL_INS = "INSERT INTO tmd_executor_log(id,tmd_executor_bo,database_source,database_target,tmd_task_log_bo,executed_date,executed_time,executed_result,run_state,delete_after) VALUES (?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_UPD = "UPDATE tmd_executor_log SET tmd_executor_bo=?,database_source=?,database_target=?,tmd_task_log_bo=?,executed_date=?,executed_time=?,executed_result=?,run_state=?,delete_after=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tmd_executor_bo,database_source,database_target,tmd_task_log_bo,executed_date,executed_time,executed_result,run_state,delete_after FROM tmd_executor_log ";

    private final Connection conn;

    public TmdExecutorLogDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(TmdExecutorLog data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setString(2, data.getTmdExecutorBo());
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

    public int insert(List<TmdExecutorLog> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (TmdExecutorLog data : dataList) {
                ps.setString(1, data.getId());
                ps.setString(2, data.getTmdExecutorBo());
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

    public int update(TmdExecutorLog data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setString(1, data.getTmdExecutorBo());
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

    public int update(List<TmdExecutorLog> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (TmdExecutorLog data : dataList) {
                ps.setString(1, data.getTmdExecutorBo());
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
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM tmd_executor_log WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public TmdExecutorLog selectByPK(String id) throws SQLException {
        TmdExecutorLog result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<TmdExecutorLog> selectAll() throws SQLException {
        ArrayList<TmdExecutorLog> result = new ArrayList<TmdExecutorLog>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private TmdExecutorLog convert(ResultSet rs) throws SQLException {
        TmdExecutorLog data = new TmdExecutorLog();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTmdExecutorBo(rs.getString(index++));
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