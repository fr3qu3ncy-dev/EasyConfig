package de.fr3qu3ncy.easyconfig.data.map;

import de.fr3qu3ncy.easyconfig.data.DataWriter;
import lombok.Getter;

import java.util.LinkedHashMap;

public class MapDataWriter extends DataWriter {

    @Getter
    private final LinkedHashMap<Object, Object> map;

    public MapDataWriter(String key, LinkedHashMap<Object, Object> map) {
        super(key);
        this.map = map;
    }

    @Override
    public <T> void writeData(T object) {
        map.put(key, object);
    }

    @Override
    public <T> void writeData(String key, T object) {
        map.put(key, object);
    }

    @Override
    public DataWriter getChildWriter(String key) {
        return new MapDataWriter(key, map);
    }
}
