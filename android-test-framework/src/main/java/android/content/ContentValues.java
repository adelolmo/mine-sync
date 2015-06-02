package android.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Andoni del Olmo
 * @since 30.05.15
 */
public class ContentValues {

    private Map<String, Object> mValues;

    public ContentValues() {
        mValues = new HashMap<String, Object>();
    }

    public void put(String key, String value) {
        mValues.put(key, value);
    }

    public void put(String key, Boolean value) {
        mValues.put(key, value);
    }

    public void put(String key, Long value) {
        mValues.put(key, value);
    }

    public void put(String key, Integer value) {
        mValues.put(key, value);
    }

    public Set<Map.Entry<String, Object>> valueSet() {
        return mValues.entrySet();
    }

    public Set<String> keySet() {
        return mValues.keySet();
    }

    public Object get(String key) {
        return mValues.get(key);
    }
}