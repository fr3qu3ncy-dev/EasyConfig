package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SpigotFileConfig extends SpigotConfigSection implements FileConfiguration {

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
    @SneakyThrows
    public void save(File file) {
        yamlConfig.save(file);
    }
}
