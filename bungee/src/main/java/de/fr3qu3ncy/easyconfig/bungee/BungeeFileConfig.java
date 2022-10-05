package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.FileConfig;
import lombok.SneakyThrows;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

public class BungeeFileConfig extends BungeeConfigSection implements FileConfig {

    private final net.md_5.bungee.config.Configuration config;

    public BungeeFileConfig(net.md_5.bungee.config.Configuration config, String path, String name) {
        super(config, path, name);
        this.config = config;
    }

    @Override
    public void setMaxWidth(int width) {
        //Not available on Bungee
    }

    @Override
    public void setCopyDefaults(boolean value) {
        //Not available on Bungee
    }

    @Override
    @SneakyThrows
    public void save(File file) {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
    }
}
