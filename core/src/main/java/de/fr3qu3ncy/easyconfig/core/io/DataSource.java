package de.fr3qu3ncy.easyconfig.core.io;

import java.util.List;

public interface DataSource {

    <T> T getData(String key);

    List<String> getKeys(String path);
}
