package de.fr3qu3ncy.easyconfig.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class DataSource {

    @Getter
    protected final String key;

    public abstract <T> T getData();

    public abstract <T> T getData(String key);

    public abstract DataSource getChildSource(String key);

}
