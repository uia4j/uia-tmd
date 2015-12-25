package uia.tmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereLike extends Where {

    private Object value;

    public WhereLike(String name, Object value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getId() {
        return "like";
    }

    @Override
    public String sql() {
        return this.name + " like ?";
    }

    @Override
    public int addParameters(PreparedStatement stmt, int index) throws SQLException {
        stmt.setObject(index, this.value);
        return index + 1;
    }

    @Override
    public Object getParamValue() {
        return this.value;
    }

}
