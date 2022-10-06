package de.fr3qu3ncy.easyconfig.core;

import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;
import de.fr3qu3ncy.easyconfig.core.data.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.core.data.config.ConfigDataWriter;
import de.fr3qu3ncy.easyconfig.core.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.util.ConfigUtils;
import de.fr3qu3ncy.easyconfig.core.util.GroupUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class ConfigIO {

    public static final String COMMENT_START_IDENTIFIER = "_COMMENT_START_";
    public static final String COMMENT_IDENTIFIER = "_COMMENT_";
    public static final String GROUP_IDENTIFIER = "_GROUP_";
    public static final String GROUP_HEADER_IDENTIFIER = "_GROUP_HEADER_";

    private ConfigIO() {}

    private static <T> void serialize(@Nonnull ConfigSerializer<T> serializer, @Nonnull SerializationInfo<?> info,
                                      @Nonnull DataWriter writer,
                                      T value) {
        serializer.serialize(info, writer, value);
    }

    private static <T> T deserialize(@Nonnull ConfigSerializer<T> serializer, @Nonnull SerializationInfo<?> info,
                                     @Nonnull DataSource source) {
        return serializer.deserialize(info, source);
    }

    /**
     * Extract values from @HolderField and save them in config
     */
    public static void saveInConfig(@Nonnull EasyConfig config, @Nonnull HolderField holderField) {
        String path = holderField.getPath();
        @Nonnull Object defaultValue = holderField.getDefaultValue();

        set(config, path, defaultValue, holderField);
    }

    @SneakyThrows
    public static void writeToField(@Nonnull HolderField holderField) {
        Object deserializedObject = get(holderField);

        holderField.getField().set(null, deserializedObject);
    }

    public static void set(@Nonnull EasyConfig config, @Nonnull String path, @Nonnull Object value,
                           @Nonnull HolderField holderField) {
        //Check if value class has serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(holderField.getFieldType());

        //Create ConfigLocation
        ConfigLocation location = ConfigUtils.createLocation(config, path);

        //Save group
        saveGroup(location, holderField.getGroup());

        //Save group header
        saveGroupHeader(location, holderField.getGroup(), holderField.isWriteGroupHeader());

        //Save comment
        saveComment(location, holderField.getComment());

        if (serializer != null) {
            //Class has serializer
            serialize(serializer,
                new SerializationInfo<>(
                    config,
                    holderField.getFieldType(),
                    value,
                    holderField.getField()
                ),
                new ConfigDataWriter(location.getName(), location.getSection()),
                value);
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
            return deserialize(serializer,
                new SerializationInfo<>(
                    holderField.getConfig(),
                    holderField.getFieldType(),
                    holderField.getDefaultValue(),
                    holderField.getField()),
                new ConfigDataSource(location.getName(), location.getSection()));
        } else {
            //Class doesn't have serializer
            return holderField.getConfig().getFileConfig().get(holderField.getPath(), holderField.getDefaultValue());
        }
    }

    public static <T> T get(@Nonnull EasyConfig config, @Nonnull String path, @Nonnull Class<T> clazz, Type genericType, T defaultValue) {
        //Check if value class has serializer
        ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(clazz);

        //Create ConfigLocation
        ConfigLocation location = ConfigUtils.createLocation(config, path);

        if (serializer != null) {
            //Class has serializer
            return (T) deserialize(serializer,
                new SerializationInfo<>(config, genericType, defaultValue, null),
                new ConfigDataSource(location.getName(), location.getSection()));
        } else {
            //Class doesn't have serializer
            return config.getFileConfig().get(path, defaultValue);
        }
    }

    private static void saveComment(@Nonnull ConfigLocation location, @Nullable String comment) {
        if (comment != null) {
            handleMultiLineComment(location, comment);
        }
    }

    private static void saveGroup(@Nonnull ConfigLocation location, @Nullable String group) {
        if (group != null) {
            location.setSingle(location.getName() + GROUP_IDENTIFIER, group);
        }
    }

    private static void saveGroupHeader(@Nonnull ConfigLocation location, @Nullable String header, boolean write) {
        if (header != null && write) {
            handleMultiLineHeader(location, header);
        }
    }

    private static void handleMultiLineComment(ConfigLocation location, String comment) {
        int i = 0;
        for (String line : comment.split("\n")) {
            location.setSingle(location.getName() + "_" + i + "_" +
                (i == 0 ? COMMENT_START_IDENTIFIER : COMMENT_IDENTIFIER), line);
            i++;
        }
    }

    private static void handleMultiLineHeader(ConfigLocation location, String header) {
        int i = 0;
        for (String line : GroupUtils.createHeader(header.split("\n"))) {
            location.setSingle(location.getName() + "_" + i + "_" + GROUP_HEADER_IDENTIFIER, line);
            i++;
        }
    }
}
