package de.fr3qu3ncy.easyconfig.data.map;

import de.fr3qu3ncy.easyconfig.data.DataSource;

import java.util.LinkedHashMap;

public class MapDataSource extends DataSource {

    private final LinkedHashMap<Object, Object> map;

    public MapDataSource(String key, LinkedHashMap<Object, Object> map) {
        super(key);
        this.map = map;
    }

    @Override
    public <T> T getData() {
        return (T) map.get(key);
    }

    @Override
    public <T> T getData(String key) {
        return (T) map.get(key);
    }

    @Override
    public DataSource getChildSource(String key) {
        return new MapDataSource(key, map);
    }
}
