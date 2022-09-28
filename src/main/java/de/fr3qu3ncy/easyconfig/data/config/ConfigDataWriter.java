package de.fr3qu3ncy.easyconfig.data.config;

import de.fr3qu3ncy.easyconfig.data.DataWriter;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigDataWriter extends DataWriter {

    private final ConfigurationSection section;

    public ConfigDataWriter(String key, ConfigurationSection section) {
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
