package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.data.DataSource;
import de.fr3qu3ncy.easyconfig.data.DataWriter;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;

import javax.annotation.Nonnull;

public class EnumSerializer implements ConfigSerializer<Enum<?>> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull Enum<?> value) {
        writer.writeData(value.name());
    }

    @Override
    public Enum<?> deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        Class<? extends Enum> en = (Class<? extends Enum>) info.getType();
        return getInstance(source.getData(), en);
    }

    public static <T extends Enum<T>> Enum<T> getInstance(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }
}
