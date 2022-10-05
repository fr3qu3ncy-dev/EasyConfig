package de.fr3qu3ncy.easyconfig.spigot;

import de.fr3qu3ncy.easyconfig.core.FileConfigSection;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SpigotConfigSection implements FileConfigSection {

    private final ConfigurationSection section;

    @Override
    public String getCurrentPath() {
        return section.getCurrentPath();
    }

    @Override
    public void set(String path, Object value) {
        section.set(path, value);
    }

    @Override
    public <T> T get(String path) {
        return (T) section.get(path);
    }

    @Override
    public <T> T get(String path, T defaultValue) {
        return (T) section.get(path, defaultValue);
    }

    @Override
    public boolean contains(String path) {
        return section.contains(path);
    }

    @Override
    public FileConfigSection getConfigurationSection(String name) {
        return new SpigotConfigSection(section.getConfigurationSection(name));
    }

    @Override
    public FileConfigSection createSection(String name) {
        return new SpigotConfigSection(section.createSection(name));
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(section.getKeys(false));
    }
}
