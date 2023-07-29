package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.configuration.FileConfiguration;
import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.io.list.ListDataSource;
import de.fr3qu3ncy.easyconfig.core.io.map.MapDataWriter;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapSerializer implements ConfigSerializer<Map<?, ?>> {

    private void serialize(String path, @Nonnull EasyConfig config, DataWriter writer, @Nonnull Type type,
                           @Nonnull Object obj, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            config.debug("Serializing map at path " + path + " with value type " + type.getTypeName());
            serializer.serialize(path, writer, obj, new SerializationInfo<>(config, type, null, field, false));
        } else {
            writer.writeData(path, obj);
        }
    }

    private Object deserialize(String path, @Nonnull EasyConfig config, @Nonnull DataSource source,
                               @Nonnull Type type, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            return serializer.deserialize(path, source, new SerializationInfo<>(config, type, null, field, false));
        } else {
            return source.getData(path);
        }
    }

    private Object deserializeKey(Object key, Type keyType, ConfigSerializer<Object> keySerializer, SerializationInfo<?> info) {
        return keySerializer.deserialize("0", new ListDataSource(List.of(key)),
            new SerializationInfo<>(info.config(), keyType, null, null, false));
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull Map<?, ?> map, SerializationInfo<? extends Map<?, ?>> info) {
        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        map.forEach((key, value) -> {
            String keyString;
            if (!(key instanceof String string)) {
                MapDataWriter mapWriter = new MapDataWriter(new LinkedHashMap<>());
                keySerializer.serialize("key", mapWriter, key, new SerializationInfo<>(info.config(), keyType, null, null, false));
                Object serializedKey = mapWriter.getMap().get("key");
                if (!(serializedKey instanceof String)) {
                    throw new IllegalArgumentException("Could not serialize type " + keyType + " to a String");
                }
                keyString = (String) serializedKey;
            } else {
                keyString = string;
            }

            serialize(path + "." + keyString, info.config(), writer, valueType, value, info.field(), valueSerializer);
        });
    }

    @Override
    public Map<Object, ?> deserialize(String path, DataSource source, SerializationInfo<Map<?, ?>> info) {
        Map<Object, Object> map;
        try {
            map = (Map<Object, Object>) ReflectionUtils.typeToClass(info.type()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            map = new LinkedHashMap<>();
        }

        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        info.config().debug("Deserializing map at path " + path + " with value type " + valueType.getTypeName());

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        FileConfiguration children = info.config().getFileConfig().getChild(path);
        if (children != null) {
            for (String key : children.getKeys()) {
                map.put(
                    deserializeKey(key, keyType, keySerializer, info),
                    deserialize(path + "." + key, info.config(), source, valueType, info.field(), valueSerializer)
                );
            }
            return map;
        }

        Map<Object, Object> returnMap = map;
        try {
            Map<String, ?> data = source.getData(path);
            if (data == null) return map;

            data.forEach((key, value) -> returnMap.put(
                deserializeKey(key, keyType, keySerializer, info),
                deserialize(path + "." + key, info.config(), source, valueType, info.field(), valueSerializer)
            ));
        } catch (ClassCastException ex) {
            return returnMap;
        }
        return returnMap;
    }
}
