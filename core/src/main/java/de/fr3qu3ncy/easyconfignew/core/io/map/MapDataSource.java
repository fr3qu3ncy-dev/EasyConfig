package de.fr3qu3ncy.easyconfignew.core.io.map;

import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;

@SuppressWarnings("unchecked")
@AllArgsConstructor
public class MapDataSource implements DataSource {

    private final LinkedHashMap<String, Object> map;

    @Override
    public <T> T getData(String key) {
        return (T) map.get(key);
    }
}
