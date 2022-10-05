package de.fr3qu3ncy.easyconfig.core;

import java.util.List;

public interface FileConfigSection {

    String getCurrentPath();

    void set(String path, Object value);

    <T> T get(String path);

    <T> T get(String path, T defaultValue);

    boolean contains(String path);

    FileConfigSection getConfigurationSection(String name);

    FileConfigSection createSection(String name);

    List<String> getKeys();
}
