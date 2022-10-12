package de.fr3qu3ncy.easyconfignew.core.configuration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public interface FileConfiguration {

    boolean contains(String path);

    <T> T get(String path);
    <T> T get(String path, T defaultValue);

    void set(String path, Object object);

    @Nullable
    FileConfiguration getChild(String path);

    List<String> getKeys();

    void setMaxWidth(int width);

    void save(File file);
}
