package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.data.DataSource;
import de.fr3qu3ncy.easyconfig.data.DataWriter;
import de.fr3qu3ncy.easyconfig.data.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.util.ReflectionUtils;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MapSerializer implements ConfigSerializer<Map<?, ?>> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull Map<?, ?> map) {
        List<Type> types = ReflectionUtils.getGenericTypes(info.getType(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        AtomicInteger index = new AtomicInteger();
        map.forEach((key, value) -> {
            DataWriter childWriter = writer.getChildWriter(String.valueOf(index.get()));
            serialize(childWriter, "key", keyType, key, info.getField(), keySerializer);
            serialize(childWriter, "value", valueType, value, info.getField(), valueSerializer);

            index.getAndIncrement();
        });
    }

    private void serialize(DataWriter writer, @Nonnull String name, @Nonnull Type type,
                           @Nonnull Object obj, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            serializer.serialize(new SerializationInfo<>(type, null, field), writer, obj);
        } else {
            writer.writeData(name, obj);
        }
    }

    private Object deserialize(@Nonnull DataSource source, @Nonnull String name, @Nonnull Type type, @Nullable Field field,
                               @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            return serializer.deserialize(new SerializationInfo<>(type, null, field), source);
        } else {
            return source.getData(name);
        }
    }

    @Override
    public Map<?, ?> deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        Map<Object, Object> map;
        try {
            map = (Map<Object, Object>) ReflectionUtils.typeToClass(info.getType()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            map = new HashMap<>();
        }

        List<Type> types = ReflectionUtils.getGenericTypes(info.getType(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        ConfigurationSection children = ((ConfigDataSource) source).getSection();
        if (children != null) {
            for (String index : children.getKeys(false)) {
                map.put(
                    //Deserialize key
                    deserialize(
                        source.getChildSource(String.valueOf(index)), "key", keyType, info.getField(), keySerializer),
                    //Deserialize value
                    deserialize(
                        source.getChildSource(String.valueOf(index)), "value", valueType, info.getField(), valueSerializer));
            }
        }
        return map;
    }
}
