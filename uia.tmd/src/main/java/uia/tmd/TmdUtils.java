package uia.tmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uia.utils.dao.TableType;

public final class TmdUtils {

    private TmdUtils() {
    }

    public static String generateKey(String tableName, List<String> pkColumns, Map<String, Object> row) {
        String result = tableName.toUpperCase();
        for (String pkColumn : pkColumns) {
            result += ("," + row.get(pkColumn.toUpperCase()));
        }
        return result;
    }

    public static String generateKey(TableType tableType, Map<String, Object> row) {
        return generateKey(tableType.getTableName(), tableType.selectPkNames(), row);
    }

    public static List<String> generateKeys(TableType tableType, List<Map<String, Object>> rows) {
        ArrayList<String> result = new ArrayList<String>();

        List<String> pkColumns = tableType.selectPkNames();
        for (Map<String, Object> row : rows) {
            result.add(generateKey(tableType.getTableName(), pkColumns, row));
        }

        return result;
    }
}
