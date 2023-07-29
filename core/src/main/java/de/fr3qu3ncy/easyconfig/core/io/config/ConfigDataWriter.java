package de.fr3qu3ncy.easyconfig.core.io.config;

import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor
public class ConfigDataWriter implements DataWriter {

    private final FileConfiguration config;

    @Override
    public void writeData(@Nonnull String path, Object object) {
        config.set(path, object);
    }
}
