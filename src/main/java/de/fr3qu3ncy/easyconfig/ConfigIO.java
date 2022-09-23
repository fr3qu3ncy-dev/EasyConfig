package de.fr3qu3ncy.easyconfig;

import de.fr3qu3ncy.easyconfig.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.util.ConfigUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class ConfigIO {

    public static final String COMMENT_IDENTIFIER = "_COMMENT_";

    private ConfigIO() {}

    private static <T> void serialize(@Nonnull ConfigLocation location, @Nonnull ConfigSerializer<T> serializer,
                                      T value, @Nonnull SerializationInfo<?> info) {
        serializer.serialize(info, location, value);
    }

    private static <T> T deserialize(@Nonnull ConfigLocation location, @Nonnull ConfigSerializer<T> serializer,
                                     @Nonnull SerializationInfo<?> info) {
        return serializer.deserialize(info, location);
    }

    /**
     * Extract values from @HolderField and save them in config
     */
    public static void saveInConfig(@Nonnull EasyConfig config, @Nonnull HolderField holderField) {
        String path = holderField.getPath();
        String comment = holderField.getComment();
        @Nonnull Object defaultValue = holderField.getDefaultValue();

        set(config, path, holderField.getFieldType(), defaultValue, holderField.getField(), comment);
    }

    @SneakyThrows
    public static void writeToField(@Nonnull HolderField holderField) {
        Object deserializedObject = get(holderField);

        holderField.getField().set(null, deserializedObject);
    }

    public static void set(@Nonnull EasyConfig config, @Nonnull String path, @Nonnull Type type,
                           @Nonnull Object value, @Nullable Field originalField, @Nullable String comment) {
        //Check if value class has serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(type);

        //Create ConfigLocation
        ConfigLocation location = ConfigUtils.createLocation(config, path);

        //Save comment
        saveComment(location, comment);

        if (serializer != null) {
            //Class has serializer
            serialize(location, serializer, value,
                new SerializationInfo<>(
                    type,
                    value,
                    originalField
                ));
        } else {
            //Class doesn't have serializer
            location.setSingle(value);
        }

        //Save config after changes
        config.saveConfig();
    }

    public static Object get(@Nonnull HolderField holderField) {
        //Check if value class has serializer
        ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(holderField.getFieldClass());

        //Create ConfigLocation
        ConfigLocation location = ConfigUtils.createLocation(holderField.getConfig(), holderField.getPath());

        if (serializer != null) {
            //Class has serializer
            return deserialize(location, serializer,
                new SerializationInfo<>(
                    holderField.getFieldType(),
                    holderField.getDefaultValue(),
                    holderField.getField()));
        } else {
            //Class doesn't have serializer
            return holderField.getConfig().getBukkitConfig().get(holderField.getPath(), holderField.getDefaultValue());
        }
    }

    public static <T> T get(@Nonnull EasyConfig config, @Nonnull String path, @Nonnull Class<T> clazz, Type genericType, T defaultValue) {
        //Check if value class has serializer
        ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(clazz);

        //Create ConfigLocation
        ConfigLocation location = ConfigUtils.createLocation(config, path);

        if (serializer != null) {
            //Class has serializer
            return (T) deserialize(location, serializer,
                new SerializationInfo<>(
                    genericType,
                    defaultValue,
                    null));
        } else {
            //Class doesn't have serializer
            return (T) config.getBukkitConfig().get(path, defaultValue);
        }
    }

    private static void saveComment(@Nonnull ConfigLocation location, @Nullable String comment) {
        if (comment != null) {
            location.setSingle(location.getName() + COMMENT_IDENTIFIER, comment);
        }
    }
}
