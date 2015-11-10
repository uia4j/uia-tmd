package uia.tmd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MS SQL Server data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class MSSQLAccessor implements DataAccessor {

    private static final String CONN = "jdbc:sqlserver://%s:%s;databaseName=%s;";

    private final String connString;

    private Connection conn;

    /**
     * Constructor.
     * @param host Host name or ip.
     * @param port Port number.
     * @param dbName Database name.
     * @throws Exception Initial failure.
     */
    public MSSQLAccessor(String host, int port, String dbName) throws Exception {
        this.connString = String.format(CONN, host, port, dbName);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public void connect(String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(this.connString, user, password);
    }

    @Override
    public List<Map<String, Object>> select(String sql, List<Object> parameters) throws SQLException {
        ArrayList<Map<String, Object>> table = new ArrayList<Map<String, Object>>();

        // parameters
        PreparedStatement ps = this.conn.prepareStatement(sql);
        if (parameters != null) {
            for (int i = 0, n = parameters.size(); i < n; i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
        }

        // select
        ResultSet rs = ps.executeQuery();
        int cnt = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<String, Object>();
            for (int i = 0; i < cnt; i++) {
                row.put(rs.getMetaData().getColumnName(i + 1), rs.getObject(i + 1));
            }
            table.add(row);
        }

        rs.close();
        ps.close();

        return table;
    }

    @Override
    public int execueUpdate(String sql, List<Object> parameters) throws SQLException {
        if (parameters == null) {
            return 0;
        }

        PreparedStatement ps = this.conn.prepareStatement(sql);
        for (int i = 0, n = parameters.size(); i < n; i++) {
            ps.setObject(i + 1, parameters.get(i));
        }
        return ps.executeUpdate();
    }

    @Override
    public void disconnect() throws SQLException {
        try {
            this.conn.close();
        }
        finally {
            this.conn = null;
        }
    }
}
