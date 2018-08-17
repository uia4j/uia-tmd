package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import uia.tmd.zztop.db.TxKey;

public class TxKeyDao {

    private static final String SQL_INS = "INSERT INTO zzt_tx_key(id,tx_time,table_name) VALUES (?,?,?)";

    private static final String SQL_UPD = "UPDATE zzt_tx_key SET tx_time=?,table_name=? WHERE id=?";

    private static final String SQL_SEL = "SELECT id,tx_time,table_name FROM zzt_tx_key ";

    private final Connection conn;

    public TxKeyDao(Connection conn) {
        this.conn = conn;
    }

    public int insert(TxKey data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            ps.setString(1, data.getId());
            ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
            ps.setString(3, data.getTableName());

            return ps.executeUpdate();
        }
    }

    public int insert(List<TxKey> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_INS)) {
            for (TxKey data : dataList) {
                ps.setString(1, data.getId());
                ps.setTimestamp(2, new Timestamp(data.getTxTime().getTime()));
                ps.setString(3, data.getTableName());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int update(TxKey data) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
            ps.setString(2, data.getTableName());
            ps.setString(3, data.getId());

            return ps.executeUpdate();
        }
    }

    public int update(List<TxKey> dataList) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_UPD)) {
            for (TxKey data : dataList) {
                ps.setTimestamp(1, new Timestamp(data.getTxTime().getTime()));
                ps.setString(2, data.getTableName());
                ps.setString(3, data.getId());

                ps.addBatch();
            }
            return ps.executeBatch().length;
        }
    }

    public int delete(String id) throws SQLException {
        try (PreparedStatement ps = this.conn.prepareStatement("DELETE FROM zzt_tx_key WHERE id=?")) {
            ps.setString(1, id);

            return ps.executeUpdate();
        }
    }

    public TxKey selectByPK(String id) throws SQLException {
        TxKey result = null;
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE id=?")) {
            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = convert(rs);
            }
            return result;
        }
    }

    public List<TxKey> selectAll() throws SQLException {
        ArrayList<TxKey> result = new ArrayList<TxKey>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    public List<TxKey> selectByTable(String tableName) throws SQLException {
        ArrayList<TxKey> result = new ArrayList<TxKey>();
        try (PreparedStatement ps = this.conn.prepareStatement(SQL_SEL + "WHERE table_name=?")) {
            ps.setString(1, tableName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(convert(rs));
            }
            return result;
        }
    }

    private TxKey convert(ResultSet rs) throws SQLException {
        TxKey data = new TxKey();

        int index = 1;
        data.setId(rs.getString(index++));
        data.setTxTime(rs.getTimestamp(index++));
        data.setTableName(rs.getString(index++));

        return data;
    }
}
