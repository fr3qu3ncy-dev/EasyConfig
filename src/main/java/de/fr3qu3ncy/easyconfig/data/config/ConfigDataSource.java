package de.fr3qu3ncy.easyconfig.data.config;

import de.fr3qu3ncy.easyconfig.data.DataSource;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigDataSource extends DataSource {

    @Getter
    private final ConfigurationSection section;

    public ConfigDataSource(String key, ConfigurationSection section) {
        super(key);
        this.section = section;
    }

    @Override
    public <T> T getData() {
        return (T) section.get(key);
    }

    @Override
    public <T> T getData(String key) {
        return (T) section.get(key);
    }

    @Override
    public DataSource getChildSource(String key) {
        return new ConfigDataSource(key, section.getConfigurationSection(this.key));
    }
}
