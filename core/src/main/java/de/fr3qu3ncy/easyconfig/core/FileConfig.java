package de.fr3qu3ncy.easyconfig.core;

import java.io.File;

public interface FileConfig extends FileConfigSection {

    <T> T get(String path, T defaultValue);

    void setMaxWidth(int width);

    void setCopyDefaults(boolean value);

    void save(File file);

}
