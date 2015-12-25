package uia.tmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Where {

    protected String name;

    public Where(String name) {
        this.name = name;
    }

    public String getParamName() {
        return this.name;
    }

    public abstract Object getParamValue();

    public abstract String getId();

    public abstract String sql();

    public abstract int addParameters(PreparedStatement stmt, int index) throws SQLException;
}
