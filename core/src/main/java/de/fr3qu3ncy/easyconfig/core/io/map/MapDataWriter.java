package de.fr3qu3ncy.easyconfig.core.io.map;

import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
public class MapDataWriter implements DataWriter {

    @Getter
    @Nonnull
    private final LinkedHashMap<String, Object> map;

    @Override
    public void writeData(@Nonnull String key, Object object) {
        if (object == null) return;
        writeToMap(map, object, key.split("\\."), 0);
    }

    private void writeToMap(Map<String, Object> map, Object object, String[] nodes, int index) {
        if (index < nodes.length - 1) {
            Object childMap = map.computeIfAbsent(nodes[index], k -> new LinkedHashMap<String, Object>());

            writeToMap((Map<String, Object>) childMap, object, nodes, ++index);
            return;
        }
        map.put(nodes[index], object);
    }
}
