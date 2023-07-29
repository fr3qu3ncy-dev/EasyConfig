package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BungeeConfigSection implements FileConfiguration {

    private final Configuration config;

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public FileConfiguration getChild(String path) {
        return new BungeeConfigSection(config.getSection(path));
    }

    @Override
    public <T> T get(String path) {
        return (T) config.get(path);
    }

    @Override
    public <T> T get(String path, T defaultValue) {
        return config.get(path, defaultValue);
    }

    @Override
    public boolean contains(String path) {
        return config.contains(path);
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(config.getKeys());
    }

    @Override
    public void setMaxWidth(int width) {
        /* Not implemented */
    }

    @Override
    @SneakyThrows
    public void save(File file) {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
    }
}
