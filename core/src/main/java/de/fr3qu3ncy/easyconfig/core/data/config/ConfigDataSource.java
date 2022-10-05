package de.fr3qu3ncy.easyconfig.core.data.config;

import de.fr3qu3ncy.easyconfig.core.FileConfigSection;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import lombok.Getter;

public class ConfigDataSource extends DataSource {

    @Getter
    private final FileConfigSection section;

    public ConfigDataSource(String key, FileConfigSection section) {
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
