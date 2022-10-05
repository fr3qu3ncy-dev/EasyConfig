package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfig.core.FileConfig;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SpigotFileConfig extends SpigotConfigSection implements FileConfig {

    private final YamlConfiguration yamlConfig;

    public SpigotFileConfig(YamlConfiguration yamlConfig) {
        super(yamlConfig);
        this.yamlConfig = yamlConfig;
    }

    @Override
    public void setMaxWidth(int width) {
        yamlConfig.options().width(width);
    }

    @Override
    public void setCopyDefaults(boolean value) {
        yamlConfig.options().copyDefaults(value);
    }

    @Override
    @SneakyThrows
    public void save(File file) {
        yamlConfig.save(file);
    }
}
