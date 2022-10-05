package de.fr3qu3ncy.easyconfig.bungee;

import de.fr3qu3ncy.easyconfig.core.FileConfigSection;
import lombok.AllArgsConstructor;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BungeeConfigSection implements FileConfigSection {

    private final Configuration config;
    private final String path;
    private final String name;

    @Override
    public String getCurrentPath() {
        return path + "." + name;
    }

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
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
    public FileConfigSection getConfigurationSection(String name) {
        return new BungeeConfigSection(config.getSection(name), getCurrentPath(), name);
    }

    @Override
    public FileConfigSection createSection(String name) {
        return getConfigurationSection(name);
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(config.getKeys());
    }
}
