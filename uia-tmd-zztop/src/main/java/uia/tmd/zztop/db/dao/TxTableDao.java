package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.TxTable;

public class TxTableDao {

    private static final String SQL_INS = "INSERT INTO zzt_tx_table(id,tx_time,table_name) VALUES (?,?,?)";

    private static final String SQL_UPD = "UPDATE zzt_tx_table SET tx_time=?,table_name=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tx_time,table_name FROM zzt_tx_table ";

    private final Connection conn;

    public TxTableDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(TxTable data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
            ps.setString(3, data.getTableName());

            return ps.executeUpdate();
        }
    }

    public int insert(List<TxTable> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (TxTable data : dataList) {
                ps.setString(1, data.getId());
                ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
                ps.setString(3, data.getTableName());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(TxTable data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
            ps.setString(2, data.getTableName());
            ps.setString(3, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<TxTable> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (TxTable data : dataList) {
                ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
                ps.setString(2, data.getTableName());
                ps.setString(3, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM zzt_tx_table WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public TxTable selectLastByTable(String tableName) throws SQLException {
        TxTable result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE table_name=? ORDER BY id desc")) {
            ps.setString(1, tableName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public TxTable selectByPK(String id) throws SQLException {
        TxTable result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<TxTable> selectAll() throws SQLException {
        ArrayList<TxTable> result = new ArrayList<TxTable>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private TxTable convert(ResultSet rs) throws SQLException {
        TxTable data = new TxTable();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTxTime(rs.getTimestamp(index++));
        data.setTableName(rs.getString(index++));

        return data;
    }
}
