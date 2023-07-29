package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.configuration.StringFormatter;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;

public class BungeeConfig extends EasyConfig {

    public BungeeConfig(File configDirectory, String fileName, Class<?> holdingClass, Object holderInstance) {
        super(new BungeeFileConfig(loadConfig(configDirectory, fileName)), configDirectory, holdingClass, fileName, holderInstance);
    }

    public BungeeConfig(File configDirectory, String fileName, Class<?> holdingClass) {
        this(configDirectory, fileName, holdingClass, null);
    }

    @SneakyThrows
    private static Configuration loadConfig(File configDirectory, String fileName) {
        if (!configDirectory.isDirectory()) configDirectory.mkdirs();
        File file = new File(configDirectory, fileName + ".yml");
        if (!file.exists()) file.createNewFile();
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    @Override
    protected FileConfiguration reloadInternal(File configDirectory, String fileName) {
        return new BungeeFileConfig(loadConfig(configDirectory, fileName));
    }

    @Override
    public StringFormatter getStringFormatter() {
        return new BungeeStringFormatter();
    }
}
