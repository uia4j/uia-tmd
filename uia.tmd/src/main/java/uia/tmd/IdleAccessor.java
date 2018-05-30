package uia.tmd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.tmd.model.xml.DbServerType;
import uia.tmd.model.xml.MColumnType;
import uia.tmd.model.xml.MTableType;
import uia.utils.dao.ColumnType;
import uia.utils.dao.Database;
import uia.utils.dao.TableType;
import uia.utils.dao.where.Where;

/**
 * Idle data accessor.
 *
 * @author Kyle K. Lin
 *
 */
public class IdleAccessor implements DataAccessor, Database {

    @Override
    public void connect() throws SQLException {

    }

    @Override
    public Database getDatabase() {
        return this;
    }

    @Override
    public void disconnect() throws SQLException {
    }

    @Override
    public void beingTx() throws SQLException {

    }

    @Override
    public void endTx() throws SQLException {

    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public List<String> listTables() throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<Map<String, Object>> select(String sql, Where where) throws SQLException {
        return new ArrayList<Map<String, Object>>();
    }

    @Override
    public int execueUpdate(String sql, Map<String, Object> parameters) throws SQLException {
        System.out.println(sql + ", count: 1");
        return 1;
    }

    @Override
    public int execueBatch(String sql, List<Map<String, Object>> table) throws SQLException {
        System.out.println(sql + ", batch: " + table.size());
        return 0;
    }

    @Override
    public List<MColumnType> prepareColumns(String tableName) throws SQLException {
        return new ArrayList<MColumnType>();
    }

    @Override
    public void initial(DbServerType svrType, Map<String, MTableType> tables) {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public void setSchema(String schema) {

    }

    @Override
    public List<String> selectTableNames() throws SQLException {
        return null;
    }

    @Override
    public List<String> selectTableNames(String prefix) throws SQLException {
        return null;
    }

    @Override
    public List<String> selectViewNames() throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public List<String> selectViewNames(String prefix) throws SQLException {
        return new ArrayList<String>();
    }

    @Override
    public boolean exists(String tableOrView) throws SQLException {
        return false;
    }

    @Override
    public TableType selectTable(String tableOrView, boolean firstAsPK) throws SQLException {
        return null;
    }

    @Override
    public List<ColumnType> selectColumns(String tableName, boolean firstAsPK) throws SQLException {
        return new ArrayList<ColumnType>();
    }

    @Override
    public String selectViewScript(String viewName) throws SQLException {
        return null;
    }

    @Override
    public String generateCreateTableSQL(TableType table) {
        return null;
    }

    @Override
    public String generateAlterTableSQL(String tableName, List<ColumnType> cols) {
        return null;
    }

    @Override
    public int createTable(TableType table) throws SQLException {
        return 0;
    }

    @Override
    public int alterTableColumns(String tableName, List<ColumnType> columns) throws SQLException {
        return 0;
    }

    @Override
    public int dropTable(String tableName) throws SQLException {
        return 0;
    }

    @Override
    public int createView(String viewName, String sql) throws SQLException {
        return 0;
    }

    @Override
    public int dropView(String viewName) throws SQLException {
        return 0;
    }

    @Override
    public int[] executeBatch(List<String> sqls) throws SQLException {
        sqls.forEach(System.out::println);
        return new int[sqls.size()];
    }

    @Override
    public int[] executeBatch(String sql, List<List<Object>> rows) throws SQLException {
        System.out.println(sql + ", rows:" + rows.size());
        return new int[rows.size()];
    }
}
