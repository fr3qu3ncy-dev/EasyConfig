package de.fr3qu3ncy.easyconfig.core.serialization;

import de.fr3qu3ncy.easyconfig.core.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;

import javax.annotation.Nonnull;

public interface ConfigSerializer<T> {

    void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull T value);

    T deserialize(@Nonnull SerializationInfo<?> info, DataSource source);
}
