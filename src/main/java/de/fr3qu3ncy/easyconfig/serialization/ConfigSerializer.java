package de.fr3qu3ncy.easyconfig.serialization;

import de.fr3qu3ncy.easyconfig.ConfigLocation;
import de.fr3qu3ncy.easyconfig.SerializationInfo;

import javax.annotation.Nonnull;

public interface ConfigSerializer<T> {

    void serialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location, @Nonnull T value);

    T deserialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location);
}
