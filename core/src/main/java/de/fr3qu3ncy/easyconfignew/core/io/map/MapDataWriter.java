package de.fr3qu3ncy.easyconfignew.core.io.map;

import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

@AllArgsConstructor
public class MapDataWriter implements DataWriter {

    @Getter
    private final LinkedHashMap<String, Object> map;

    @Override
    public void writeData(@Nonnull String key, Object object) {
        map.put(key.replaceFirst(".", ""), object);
    }
}
