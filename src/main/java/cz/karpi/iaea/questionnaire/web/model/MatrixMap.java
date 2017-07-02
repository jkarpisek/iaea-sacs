package cz.karpi.iaea.questionnaire.web.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MatrixMap implements Map<String, Object> {

    private Map<String, Map<String, Object>> values = new HashMap<>();

    private static final String PROPERTY_DELIMITER = ";";

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    public static String[] parseKey(Object key) {
        return key.toString().split(PROPERTY_DELIMITER);
    }

    public static String getPropertyName(String key1, String key2) {
        return String.join(PROPERTY_DELIMITER, key1, key2);
    }

    @Override
    public boolean containsKey(Object key) {
        final String[] keys = parseKey(key);
        return values.containsKey(keys[0]) && values.get(keys[0]).containsKey(keys[1]);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key) {
        final String[] keys = parseKey(key);
        return containsKey(key) ? values.get(keys[0]).get(keys[1]) : null;
    }

    @Override
    public Object put(String key, Object value) {
        final String[] keys = parseKey(key);
        if (!values.containsKey(keys[0])) {
            values.put(keys[0], new HashMap<>());
        }
        values.get(keys[0]).put(keys[1], value);
        return value;
    }

    @Override
    public Object remove(Object key) {
        final String[] keys = parseKey(key);
        return containsKey(key) ? values.get(keys[0]).remove(keys[1]) : null;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Object> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public Map<String, Map<String, Object>> getValues() {
        return values;
    }
}
