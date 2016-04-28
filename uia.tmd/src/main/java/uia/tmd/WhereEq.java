package uia.tmd;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WhereEq extends Where {

    private final Object value;

    public WhereEq(String name, Object value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getId() {
        return "eq";
    }

    @Override
    public String sql() {
        return this.value == null ? this.name + " is null" : this.name + "=?";
    }

    @Override
    public int addParameters(PreparedStatement stmt, int index) throws SQLException {
        if (this.value != null) {
            stmt.setObject(index, this.value);
            return index + 1;
        }
        else {
            return index;
        }
    }

    @Override
    public Object getParamValue() {
        return this.value;
    }

}
