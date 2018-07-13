package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.TmdTxTable;

public class TmdTxTableDao {

    private static final String SQL_INS = "INSERT INTO tmd_tx_table(id,tx_time,table_name) VALUES (?,?,?)";

    private static final String SQL_UPD = "UPDATE tmd_tx_table SET tx_time=?,table_name=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tx_time,table_name FROM tmd_tx_table ";

    private final Connection conn;

    public TmdTxTableDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(TmdTxTable data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
            ps.setString(3, data.getTableName());

            return ps.executeUpdate();
        }
    }

    public int insert(List<TmdTxTable> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (TmdTxTable data : dataList) {
                ps.setString(1, data.getId());
                ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
                ps.setString(3, data.getTableName());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(TmdTxTable data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
            ps.setString(2, data.getTableName());
            ps.setString(3, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<TmdTxTable> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (TmdTxTable data : dataList) {
                ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
                ps.setString(2, data.getTableName());
                ps.setString(3, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM tmd_tx_table WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public TmdTxTable selectLastByTable(String tableName) throws SQLException {
        TmdTxTable result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE table_name=? ORDER BY id desc")) {
            ps.setString(1, tableName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public TmdTxTable selectByPK(String id) throws SQLException {
        TmdTxTable result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<TmdTxTable> selectAll() throws SQLException {
        ArrayList<TmdTxTable> result = new ArrayList<TmdTxTable>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private TmdTxTable convert(ResultSet rs) throws SQLException {
        TmdTxTable data = new TmdTxTable();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTxTime(rs.getTimestamp(index++));
        data.setTableName(rs.getString(index++));

        return data;
    }
}
