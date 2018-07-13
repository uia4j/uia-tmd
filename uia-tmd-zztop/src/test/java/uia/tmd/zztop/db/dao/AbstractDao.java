package uia.tmd.zztop.db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDao {

    protected Connection createConn() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/tmd", "postgres", "pgAdmin");
    }

}
