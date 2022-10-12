package de.fr3qu3ncy.easyconfignew.core.io.config;

import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfigDataWriter implements DataWriter {

    private final FileConfiguration config;

    @Override
    public void writeData(String path, Object object) {
        config.set(path, object);
    }
}
