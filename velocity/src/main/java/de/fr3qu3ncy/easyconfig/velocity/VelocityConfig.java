package de.fr3qu3ncy.easyconfig.velocity;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.configuration.StringFormatter;
import lombok.SneakyThrows;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;

public class VelocityConfig extends EasyConfig {

    private final StringFormatter stringFormatter = new VelocityStringFormatter();

    public VelocityConfig(File configDirectory, String fileName, Class<?> holdingClass, Object holderInstance) {
        super(new VelocityFileConfig(loadConfig(configDirectory, fileName)), configDirectory, holdingClass, fileName, holderInstance);
    }

    public VelocityConfig(File configDirectory, String fileName, Class<?> holdingClass) {
        this(configDirectory, fileName, holdingClass, null);
    }

    @SneakyThrows
    private static ConfigurationNode loadConfig(File configDirectory, String fileName) {
        return YAMLConfigurationLoader.builder().setFile(new File(configDirectory, fileName + ".yml")).build().load();
    }

    @Override
    protected FileConfiguration reloadInternal(File configDirectory, String fileName) {
        return new VelocityFileConfig(loadConfig(configDirectory, fileName));
    }

    @Override
    public StringFormatter getStringFormatter() {
        return stringFormatter;
    }
}
