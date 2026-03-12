import java.util.HashMap;
import java.util.Map;

public class Filter {
    private Map<String, Object> filterMap = new HashMap<String, Object>();

    Filter() {}

    public void addFilter(String rule, Object value) {
        filterMap.put(rule, value);
    }

    public void clear() {
        filterMap.clear();
    }

    public Map<String, Object> getMap() {
        return filterMap;
    }
}
