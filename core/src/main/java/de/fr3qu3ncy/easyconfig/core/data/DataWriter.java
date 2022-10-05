package de.fr3qu3ncy.easyconfig.core.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class DataWriter {

    @Getter
    protected final String key;

    public abstract <T> void writeData(T object);

    public abstract <T> void writeData(String key, T object);

    public abstract DataWriter getChildWriter(String key);
}
