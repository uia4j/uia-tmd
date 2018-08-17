package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.QtzClock;
import uia.utils.dao.DateUtils;

public class QtzClockDao {

    private static final String SQL_INS = "INSERT INTO zzt_qtz_clock(id,start_time,end_time,clock_type,clock_interval,tmd_job_bo,trigger_startup) VALUES (?,?,?,?,?,?,?)";

    private static final String SQL_UPD = "UPDATE zzt_qtz_clock SET start_time=?,end_time=?,clock_type=?,clock_interval=?,tmd_job_bo=?,trigger_startup=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,start_time,end_time,clock_type,clock_interval,tmd_job_bo,trigger_startup FROM zzt_qtz_clock ";

    private final Connection conn;

    public QtzClockDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(QtzClock data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            DateUtils.setDate(ps, 2, data.getStartTime());
            DateUtils.setDate(ps, 3, data.getEndTime());
            ps.setString(4, data.getClockType());
            ps.setInt(5, data.getClockInterval());
            ps.setString(6, data.getTmdJobBo());
            ps.setString(7, data.getTriggerStartup());

            return ps.executeUpdate();
        }
    }

    public int insert(List<QtzClock> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (QtzClock data : dataList) {
                ps.setString(1, data.getId());
                DateUtils.setDate(ps, 2, data.getStartTime());
                DateUtils.setDate(ps, 3, data.getEndTime());
                ps.setString(4, data.getClockType());
                ps.setInt(5, data.getClockInterval());
                ps.setString(6, data.getTmdJobBo());
                ps.setString(7, data.getTriggerStartup());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(QtzClock data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            DateUtils.setDate(ps, 1, data.getStartTime());
            DateUtils.setDate(ps, 2, data.getEndTime());
            ps.setString(3, data.getClockType());
            ps.setInt(4, data.getClockInterval());
            ps.setString(5, data.getTmdJobBo());
            ps.setString(6, data.getTriggerStartup());
            ps.setString(7, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<QtzClock> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (QtzClock data : dataList) {
                DateUtils.setDate(ps, 1, data.getStartTime());
                DateUtils.setDate(ps, 2, data.getEndTime());
                ps.setString(3, data.getClockType());
                ps.setInt(4, data.getClockInterval());
                ps.setString(5, data.getTmdJobBo());
                ps.setString(6, data.getTriggerStartup());
                ps.setString(7, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM zzt_qtz_clock WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public QtzClock selectByPK(String id) throws SQLException {
        QtzClock result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<QtzClock> selectAll() throws SQLException {
        ArrayList<QtzClock> result = new ArrayList<QtzClock>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private QtzClock convert(ResultSet rs) throws SQLException {
        QtzClock data = new QtzClock();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setStartTime(DateUtils.getDate(rs, index++));
        data.setEndTime(DateUtils.getDate(rs, index++));
        data.setClockType(rs.getString(index++));
        data.setClockInterval(rs.getInt(index++));
        data.setTmdJobBo(rs.getString(index++));
        data.setTriggerStartup(rs.getString(index++));

        return data;
    }
}
