package ui.tmd.zztop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    public static Connection create() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/tmd", "postgres", "pgAdmin");
    }
}
