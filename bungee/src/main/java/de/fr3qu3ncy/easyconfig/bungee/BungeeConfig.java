package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfignew.core.EasyConfig;
import de.fr3qu3ncy.easyconfignew.core.configuration.StringFormatter;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

public class BungeeConfig extends EasyConfig {

    public BungeeConfig(File configDirectory, String fileName, Class<?> holdingClass) {
        super(new BungeeFileConfig(loadConfig(configDirectory, fileName)), configDirectory, holdingClass, fileName);
    }

    @SneakyThrows
    private static Configuration loadConfig(File configDirectory, String fileName) {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(configDirectory, fileName + ".yml"));
    }

    @Override
    public StringFormatter getStringFormatter() {
        return new BungeeStringFormatter();
    }
}
