package uia.tmd.zztop.db.conf;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import uia.dao.DaoException;
import uia.dao.DaoFactory;
import uia.dao.TableDaoHelper;
import uia.dao.ViewDaoHelper;


/**
 * Database helper.
 *
 * @author Kyle K. Lin
 *
 */
public class TmdDB {

    private static final String DATASOURCE = "DATASOURCE";

    private static final String HANA = "HANA";

    private static final String ORACLE = "ORA";

    private static final String MSSQL = "MSSQL";

    private static DaoFactory factory;

    private TmdDB() {
    }

    public static Map<String, String> test() throws SQLException {
        if (factory == null) {
            return new TreeMap<>();
        }
        try (Connection conn = create()) {
            return factory.test(conn);
        }
    }

    public static Map<String, String> testSQL() throws SQLException {
        if (factory == null) {
            return new TreeMap<>();
        }
        try (Connection conn = SQLServer.create()) {
            return factory.test(conn);
        }
    }

    public static Map<String, String> testHana() throws SQLException {
        if (factory == null) {
            return new TreeMap<>();
        }
        try (Connection conn = Hana.create()) {
            return factory.test(conn);
        }
    }

    public static List<String> listTables() throws Exception {
        Set<String> tables = factory == null ? new TreeSet<>() : factory.getTables();
        ArrayList<String> result = new ArrayList<>();
        for (String t : tables) {
            result.add("" + Class.forName(t).getDeclaredField("KEY").get(null));
        }
        Collections.sort(result);
        return result;
    }

    public static List<String> listViews() throws Exception {
        Set<String> views = factory == null ? new TreeSet<>() : factory.getViews();
        ArrayList<String> result = new ArrayList<>();
        for (String v : views) {
            result.add("" + Class.forName(v).getDeclaredField("KEY").get(null));
        }
        Collections.sort(result);
        return result;
    }

    public static <T> TableDaoHelper<T> forTable(Class<T> clz) {
        initialDaoFactory();
        return factory == null ? null : factory.forTable(clz);
    }

    public static <T> ViewDaoHelper<T> forView(Class<T> clz) {
        initialDaoFactory();
        return factory == null ? null : factory.forView(clz);
    }

    public static void config() {
        initialDaoFactory();
    }

    /**
     * Configure database.
     *
     * @param conn The JDBC connection string.
     * @param user The user id.
     * @param pwd The password.
     * @throws DaoException
     */
    public static void config(String conn, String user, String pwd, String schema) {
        initialDaoFactory();
        if (DATASOURCE.equals(System.getProperty("tmd.zztop.db.env"))) {
            AppSource.config(conn);
        }
        else if (HANA.equals(System.getProperty("tmd.zztop.db.env"))) {
            Hana.config(conn, user, pwd, schema);
        }
        else if (MSSQL.equals(System.getProperty("tmd.zztop.db.env"))) {
            SQLServer.config(conn, user, pwd, schema);
        }
        else if (ORACLE.equals(System.getProperty("tmd.zztop.db.env"))) {
            Oracle.config(conn, user, pwd, schema);
        }
        else {
            PostgreSQL.config(conn, user, pwd, schema);
        }
    }

    /**
     * Create a connection.
     * @return A connection.
     * @throws SQLException Failed to execute.
     */
    public static Connection create() throws SQLException {
        if (DATASOURCE.equals(System.getProperty("tmd.zztop.db.env"))) {
            return new ConnectionProxy(AppSource.create());
        }
        else if (HANA.equals(System.getProperty("tmd.zztop.db.env"))) {
            return new ConnectionProxy(Hana.create());
        }
        else if (MSSQL.equals(System.getProperty("tmd.zztop.db.env"))) {
            return new ConnectionProxy(SQLServer.create());
        }
        else if (ORACLE.equals(System.getProperty("tmd.zztop.db.env"))) {
            return new ConnectionProxy(Oracle.create());
        }
        else {
            return new ConnectionProxy(PostgreSQL.create());
        }
    }

    public static String testEnv() {
        if (DATASOURCE.equals(System.getProperty("tmd.zztop.db.env"))) {
            return AppSource.test();
        }
        else if (HANA.equals(System.getProperty("tmd.zztop.db.env"))) {
            return Hana.test();
        }
        else if (MSSQL.equals(System.getProperty("tmd.zztop.db.env"))) {
            return SQLServer.test();
        }
        else if (ORACLE.equals(System.getProperty("tmd.zztop.db.env"))) {
            return Oracle.test();
        }
        else {
            return PostgreSQL.test();
        }
    }

    /**
     * Create a connection.
     * @return A connection.
     * @throws SQLException Failed to execute.
     */
    public static Connection createHana() throws SQLException {
        return new ConnectionProxy(Hana.create());
    }

    /**
     * Create a connection.
     * @return A connection.
     * @throws SQLException Failed to execute.
     */
    public static Connection createSQL() throws SQLException {
        return new ConnectionProxy(SQLServer.create());
    }

    /**
     * Create a connection.
     * @return A connection.
     * @throws SQLException Failed to execute.
     */
    public static Connection createOracle() throws SQLException {
        return new ConnectionProxy(Oracle.create());
    }

    private static synchronized void initialDaoFactory() {
        // ClassLoader loader
        if (factory == null) {
            factory = new DaoFactory(true);         // use TimeZone

            // Load tables and views automatically
            // Some problems in SAPME environment
            try {
            	factory.load("uia.tmd.zztop.db", TmdDB.class.getClassLoader());
            } catch (DaoException e) {

            }
        }
    }

    /**
     * Connection proxy of J2SE connection.
     *
     * @author Kyle K. Lin
     *
     */
    public static class ConnectionProxy implements Connection {

        private final Connection conn;

        ConnectionProxy(Connection conn) {
            this.conn = conn;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return this.conn.isWrapperFor(iface);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return this.conn.unwrap(iface);
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            this.conn.abort(executor);

        }

        @Override
        public void clearWarnings() throws SQLException {
            this.conn.clearWarnings();
        }

        @Override
        public void close() throws SQLException {
            if (!getAutoCommit()) {
                this.conn.rollback();
            }
            this.conn.close();
        }

        @Override
        public void commit() throws SQLException {
            this.conn.commit();
            this.conn.setAutoCommit(true);
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return this.conn.createArrayOf(typeName, elements);
        }

        @Override
        public Blob createBlob() throws SQLException {
            return this.conn.createBlob();
        }

        @Override
        public Clob createClob() throws SQLException {
            return this.conn.createClob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return this.conn.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return this.conn.createSQLXML();
        }

        @Override
        public Statement createStatement() throws SQLException {
            return this.conn.createStatement();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.conn.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return this.conn.createStruct(typeName, attributes);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return this.conn.getAutoCommit();
        }

        @Override
        public String getCatalog() throws SQLException {
            return this.conn.getCatalog();
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return this.conn.getClientInfo();
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return this.conn.getClientInfo(name);
        }

        @Override
        public int getHoldability() throws SQLException {
            return this.conn.getHoldability();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return this.conn.getMetaData();
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return this.conn.getNetworkTimeout();
        }

        @Override
        public String getSchema() throws SQLException {
            return this.conn.getSchema();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return this.conn.getTransactionIsolation();
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return this.conn.getTypeMap();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return this.conn.getWarnings();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return this.conn.isClosed();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return this.conn.isReadOnly();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return this.conn.isValid(timeout);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return this.conn.nativeSQL(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return this.conn.prepareCall(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return this.conn.prepareStatement(sql);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return this.conn.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return this.conn.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return this.conn.prepareStatement(sql, columnNames);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            this.conn.releaseSavepoint(savepoint);
        }

        @Override
        public void rollback() throws SQLException {
            this.conn.rollback();
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            this.conn.rollback(savepoint);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            this.conn.setAutoCommit(autoCommit);
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            this.conn.setCatalog(catalog);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            this.conn.setClientInfo(properties);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            this.conn.setClientInfo(name, value);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            this.conn.setHoldability(holdability);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            this.conn.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            this.conn.setReadOnly(readOnly);

        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return this.conn.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return this.conn.setSavepoint(name);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            this.conn.setSchema(schema);
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            this.conn.setTransactionIsolation(level);
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            this.conn.setTypeMap(map);
        }

    }
}
