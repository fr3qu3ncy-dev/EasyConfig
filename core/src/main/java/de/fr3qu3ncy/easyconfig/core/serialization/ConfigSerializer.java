package de.fr3qu3ncy.easyconfig.core.serialization;


import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;

import javax.annotation.Nullable;

public interface ConfigSerializer<T> {

    void serialize(String path, DataWriter writer, @Nullable T value, SerializationInfo<? extends T> info);

    T deserialize(String path, DataSource source, SerializationInfo<T> info);
}
