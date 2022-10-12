package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

public class BungeeFileConfig extends BungeeConfigSection implements FileConfiguration {

    private final Configuration config;

    public BungeeFileConfig(Configuration config) {
        super(config);
        this.config = config;
    }

    @Override
    public void setMaxWidth(int width) {
        //Not available on Bungee
    }

    @Override
    @SneakyThrows
    public void save(File file) {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
    }
}
