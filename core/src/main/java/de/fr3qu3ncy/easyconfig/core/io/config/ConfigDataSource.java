package de.fr3qu3ncy.easyconfig.core.io.config;

import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ConfigDataSource implements DataSource {

    private final FileConfiguration config;

    @Override
    public <T> T getData(String path) {
        return config.get(path);
    }

    @Override
    public List<String> getKeys(String path) {
        FileConfiguration child = config.getChild(path);
        if (child != null) {
            return child.getKeys();
        }
        return Collections.emptyList();
    }
}
