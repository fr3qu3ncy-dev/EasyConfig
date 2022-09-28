package de.fr3qu3ncy.easyconfig.serialization;

import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.data.DataSource;
import de.fr3qu3ncy.easyconfig.data.DataWriter;

import javax.annotation.Nonnull;

public interface ConfigSerializer<T> {

    void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull T value);

    T deserialize(@Nonnull SerializationInfo<?> info, DataSource source);
}
