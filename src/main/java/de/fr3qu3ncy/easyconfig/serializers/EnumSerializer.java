package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.ConfigLocation;
import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;

import javax.annotation.Nonnull;

public class EnumSerializer implements ConfigSerializer<Enum<?>> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location, @Nonnull Enum<?> value) {
        location.setSingle(value.name());
    }

    @Override
    public Enum<?> deserialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location) {
        Class<? extends Enum> en = (Class<? extends Enum>) info.getType();
        return getInstance(location.getSingle(), en);
    }

    public static <T extends Enum<T>> Enum<T> getInstance(final String value, final Class<T> enumClass) {
        return Enum.valueOf(enumClass, value);
    }
}
