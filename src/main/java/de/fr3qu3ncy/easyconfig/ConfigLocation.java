package de.fr3qu3ncy.easyconfig;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;

public class ConfigLocation {

    private final EasyConfig config;

    @Nonnull @Getter
    private final ConfigurationSection section;

    @Getter
    private final String name;

    public ConfigLocation(EasyConfig config, @Nonnull ConfigurationSection section, String name) {
        this.config = config;
        this.section = section;
        this.name = name;
    }

    public String getPath() {
        return section.getCurrentPath() + "." + name;
    }

    public ConfigLocation getChild(String childName) {
        return new ConfigLocation(config, getSection(name), childName);
    }

    public <T> T getSingle() {
        return (T) section.get(name);
    }

    public Object getSingle(String pathName) {
        return section.get(pathName);
    }

    public <T> T getInSection(String name) {
        return (T) getChild(name).getSingle(name);
    }

    public void setInSection(Object value) {
        getSection(name).set(name, value);
    }

    public void setInSection(String pathName, Object value) {
        getSection(name).set(pathName, value);
    }

    public void setSingle(String pathName, Object value) {
        section.set(pathName, value);
    }

    public void setSingle(Object value) {
        section.set(name, value);
    }

    @Nonnull
    private ConfigurationSection getSection(@Nonnull String sectionName) {
        ConfigurationSection childSection = section.getConfigurationSection(sectionName);
        return childSection != null ? childSection : section.createSection(sectionName);
    }

    public void saveConfig() {
        config.saveConfig();
    }
}
