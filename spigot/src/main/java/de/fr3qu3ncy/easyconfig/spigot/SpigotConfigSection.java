package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SpigotConfigSection implements FileConfiguration {

    @Nonnull
    private final ConfigurationSection config;

    @Override
    public void set(String path, Object value) {
        config.set(path, value);
    }

    @Override
    public FileConfiguration getChild(String path) {
        ConfigurationSection child = config.getConfigurationSection(path);
        return child != null ? new SpigotConfigSection(child) : null;
    }

    @Override
    public <T> T get(String path) {
        return (T) config.get(path);
    }

    @Override
    public <T> T get(String path, T defaultValue) {
        return (T) config.get(path, defaultValue);
    }

    @Override
    public boolean contains(String path) {
        return config.contains(path);
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(config.getKeys(false));
    }

    @Override
    public void setMaxWidth(int width) {
        /* Not implemented */
    }

    @Override
    public void save(File file) {
        /* Not implemented */
    }
}
