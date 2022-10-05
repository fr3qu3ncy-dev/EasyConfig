package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.FileConfigSection;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;
import de.fr3qu3ncy.easyconfig.core.data.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.core.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MapSerializer implements ConfigSerializer<Map<?, ?>> {

    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull Map<?, ?> map) {
        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        AtomicInteger index = new AtomicInteger();
        map.forEach((key, value) -> {
            DataWriter childWriter = writer.getChildWriter(String.valueOf(index.get()));
            serialize(info.config(), childWriter, "key", keyType, key, info.field(), keySerializer);
            serialize(info.config(), childWriter, "value", valueType, value, info.field(), valueSerializer);

            index.getAndIncrement();
        });
    }

    private void serialize(@Nonnull EasyConfig config, DataWriter writer, @Nonnull String name, @Nonnull Type type,
                           @Nonnull Object obj, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            serializer.serialize(new SerializationInfo<>(config, type, null, field), writer, obj);
        } else {
            writer.writeData(name, obj);
        }
    }

    private Object deserialize(@Nonnull EasyConfig config, @Nonnull DataSource source, @Nonnull String name,
                               @Nonnull Type type, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            return serializer.deserialize(new SerializationInfo<>(config, type, null, field), source);
        } else {
            return source.getData(name);
        }
    }

    @Override
    public Map<?, ?> deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        Map<Object, Object> map;
        try {
            map = (Map<Object, Object>) ReflectionUtils.typeToClass(info.type()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            map = new HashMap<>();
        }

        List<Type> types = ReflectionUtils.getGenericTypes(info.type(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        FileConfigSection children = ((ConfigDataSource) source).getSection();
        if (children != null) {
            for (String index : children.getKeys()) {
                map.put(
                    //Deserialize key
                    deserialize(info.config(),
                        source.getChildSource(String.valueOf(index)), "key", keyType, info.field(), keySerializer),
                    //Deserialize value
                    deserialize(info.config(),
                        source.getChildSource(String.valueOf(index)), "value", valueType, info.field(), valueSerializer));
            }
        }
        return map;
    }
}
