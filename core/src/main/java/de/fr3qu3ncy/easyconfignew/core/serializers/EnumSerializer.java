package de.fr3qu3ncy.easyconfignew.core.serializers;

import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;

import javax.annotation.Nonnull;

public class EnumSerializer implements ConfigSerializer<Enum<?>> {

    public static <T extends Enum<T>> Enum<T> getInstance(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull Enum<?> value, SerializationInfo<Enum<?>> info) {
        writer.writeData(path, value.name());
    }

    @Override
    public Enum<?> deserialize(String path, DataSource source, SerializationInfo<Enum<?>> info) {
        Class<? extends Enum> en = (Class<? extends Enum>) info.type();
        return getInstance(source.getData(path), en);
    }
}
