package de.fr3qu3ncy.easyconfig.core;

import java.io.File;

public interface FileConfig extends FileConfigSection {

    <T> T get(String path, T defaultValue);

    boolean contains(String path);

    void setMaxWidth(int width);

    void setCopyDefaults(boolean value);

    void save(File file);

}
