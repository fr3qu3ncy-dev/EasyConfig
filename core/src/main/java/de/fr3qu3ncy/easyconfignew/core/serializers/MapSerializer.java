package de.fr3qu3ncy.easyconfignew.core.serializers;

import de.fr3qu3ncy.easyconfignew.core.EasyConfig;
import de.fr3qu3ncy.easyconfignew.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapSerializer implements ConfigSerializer<Map<String, ?>> {

    private void serialize(String path, @Nonnull EasyConfig config, DataWriter writer, @Nonnull Type type,
                           @Nonnull Object obj, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            config.getLogger().info("Serializing map at path " + path + " with value type " + type.getTypeName());
            serializer.serialize(path, writer, obj, new SerializationInfo<>(config, type, null, field));
        } else {
            writer.writeData(path, obj);
        }
    }

    private Object deserialize(String path, @Nonnull EasyConfig config, @Nonnull DataSource source,
                               @Nonnull Type type, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            return serializer.deserialize(path, source, new SerializationInfo<>(config, type, null, field));
        } else {
            return source.getData(path);
        }
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull Map<String, ?> map, SerializationInfo<Map<String, ?>> info) {
        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        map.forEach((key, value) -> serialize(path + "." + key, info.config(), writer, valueType, value, info.field(), valueSerializer));
    }

    @Override
    public Map<String, ?> deserialize(String path, DataSource source, SerializationInfo<Map<String, ?>> info) {
        Map<String, Object> map;
        try {
            map = (Map<String, Object>) ReflectionUtils.typeToClass(info.type()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            map = new HashMap<>();
        }

        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        FileConfiguration children = info.config().getFileConfig().getChild(path);
        if (children != null) {
            for (String key : children.getKeys()) {
                map.put(key, deserialize(path + "." + key, info.config(),
                    source, valueType, info.field(), valueSerializer));
            }
        }
        return map;
    }
}
