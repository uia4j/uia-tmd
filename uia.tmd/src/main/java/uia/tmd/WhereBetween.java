package uia.tmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereBetween extends Where {

    private Object start;

    private Object end;

    public WhereBetween(String name, Object start, Object end) {
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public String getId() {
        return "between";
    }

    @Override
    public String sql() {
        return this.name + " between ? and ?";
    }

    @Override
    public int addParameters(PreparedStatement stmt, int index) throws SQLException {
        stmt.setObject(index, this.start);
        stmt.setObject(index + 1, this.end);
        return index + 2;

    }

    @Override
    public Object getParamValue() {
        return this.start + " and " + this.end;
    }

}
