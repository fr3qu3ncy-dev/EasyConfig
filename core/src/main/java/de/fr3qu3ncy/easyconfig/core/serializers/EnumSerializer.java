package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;

import javax.annotation.Nullable;

public class EnumSerializer implements ConfigSerializer<Enum<?>> {

    public static <T extends Enum<T>> Enum<T> getInstance(@Nullable String value, Class<T> enumClass) {
        if (value == null) return null;
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nullable Enum<?> value, SerializationInfo<? extends Enum<?>> info) {
        ConfigSerializer<Object> serializer = ConfigRegistry.getDefaultSerializer();

        String enumName = value != null ? value.name() : null;
        serializer.serialize(path, writer, enumName, info);
    }

    @Override
    public Enum<?> deserialize(String path, DataSource source, SerializationInfo<Enum<?>> info) {
        Class<? extends Enum> en = (Class<? extends Enum>) info.type();
        return getInstance(source.getData(path), en);
    }
}
