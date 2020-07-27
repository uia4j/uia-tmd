package uia.tmd;

import java.util.List;
import java.util.Map;

public interface SourceSelectFilter {

    public List<Map<String, Object>> accept(List<Map<String, Object>> sourceRows);
}
