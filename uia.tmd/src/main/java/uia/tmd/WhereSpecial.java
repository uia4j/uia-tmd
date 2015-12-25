package uia.tmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereSpecial extends Where {

    private String where;

    public WhereSpecial(String name, String where) {
        super(name);
        this.where = where;
    }

    @Override
    public String getId() {
        return "special";
    }

    @Override
    public String sql() {
        return this.where;
    }

    @Override
    public int addParameters(PreparedStatement stmt, int index) throws SQLException {
        return index;
    }

    @Override
    public Object getParamValue() {
        return this.where;
    }

}
