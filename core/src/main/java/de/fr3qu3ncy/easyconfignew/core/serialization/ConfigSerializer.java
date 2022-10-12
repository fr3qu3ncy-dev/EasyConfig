package de.fr3qu3ncy.easyconfignew.core.serialization;


import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;

import javax.annotation.Nonnull;

public interface ConfigSerializer<T> {

    void serialize(String path, DataWriter writer, @Nonnull T value, SerializationInfo<T> info);

    T deserialize(String path, DataSource source, SerializationInfo<T> info);
}
