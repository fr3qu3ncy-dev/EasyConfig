package de.fr3qu3ncy.easyconfig.core.io.map;

import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
@AllArgsConstructor
public class MapDataSource implements DataSource {

    private final LinkedHashMap<String, Object> map;

    @Override
    public <T> T getData(String key) {
        if (key.startsWith(".")) key = key.replaceFirst("\\.", "");
        return getFromMap(map, key.split("\\."), 0);
    }

    @Override
    public List<String> getKeys(String path) {
        return map.keySet().stream().toList();
    }

    private <T> T getFromMap(Map<String, Object> map, String[] nodes, int index) {
        if (index < nodes.length - 1) {
            return getFromMap((Map<String, Object>) map.get(nodes[index]), nodes, ++index);
        }
        if (map == null) return null;
        return (T) map.get(nodes[index]);
    }
}
