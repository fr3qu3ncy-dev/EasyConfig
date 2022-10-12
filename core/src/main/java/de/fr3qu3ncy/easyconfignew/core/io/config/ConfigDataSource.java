package de.fr3qu3ncy.easyconfignew.core.io.config;

import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigDataSource implements DataSource {

    private final FileConfiguration config;

    @Override
    public <T> T getData(String path) {
        return config.get(path);
    }
}
