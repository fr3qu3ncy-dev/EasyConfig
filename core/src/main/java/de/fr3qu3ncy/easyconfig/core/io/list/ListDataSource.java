package de.fr3qu3ncy.easyconfig.core.io.list;

import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ListDataSource implements DataSource {

    private final List<Object> list;

    @Override
    public <T> T getData(String key) {
        try {
            int index = Integer.parseInt(key);
            return (T) list.get(index);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public List<String> getKeys(String path) {
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            keys.add(String.valueOf(i));
        }
        return keys;
    }
}
