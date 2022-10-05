package de.fr3qu3ncy.easyconfig.core.data.config;

import de.fr3qu3ncy.easyconfig.core.FileConfigSection;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;

public class ConfigDataWriter extends DataWriter {

    private final FileConfigSection section;

    public ConfigDataWriter(String key, FileConfigSection section) {
        super(key);
        this.section = section;
    }

    @Override
    public <T> void writeData(T object) {
        section.set(key, object);
    }

    @Override
    public <T> void writeData(String key, T object) {
        section.set(key, object);
    }

    @Override
    public DataWriter getChildWriter(String key) {
        return new ConfigDataWriter(key, section.getConfigurationSection(this.key));
    }
}
