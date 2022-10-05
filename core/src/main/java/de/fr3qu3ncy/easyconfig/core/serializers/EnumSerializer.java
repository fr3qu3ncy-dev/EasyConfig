package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;

import javax.annotation.Nonnull;

public class EnumSerializer implements ConfigSerializer<Enum<?>> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull Enum<?> value) {
        writer.writeData(value.name());
    }

    @Override
    public Enum<?> deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        Class<? extends Enum> en = (Class<? extends Enum>) info.type();
        return getInstance(source.getData(), en);
    }

    public static <T extends Enum<T>> Enum<T> getInstance(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }
}
