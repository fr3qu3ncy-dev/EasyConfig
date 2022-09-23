package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.ConfigLocation;
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
    public void serialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location, @Nonnull Map<?, ?> map) {
        List<Type> types = ReflectionUtils.getGenericTypes(info.getType(), 2, "Unknown map type!");
        Type keyType = types.get(0);
        Type valueType = types.get(1);

        //Check (existing) serializer
        ConfigSerializer<Object> keySerializer = ConfigRegistry.getSerializer(keyType);
        ConfigSerializer<Object> valueSerializer = ConfigRegistry.getSerializer(valueType);

        AtomicInteger index = new AtomicInteger();
        map.forEach((key, value) -> {
            ConfigLocation childLocation = location.getChild(String.valueOf(index.get()));
            serialize(childLocation, "key", keyType, key, info.getField(), keySerializer);
            serialize(childLocation, "value", valueType, value, info.getField(), valueSerializer);

            index.getAndIncrement();
        });
    }

    private void serialize(@Nonnull ConfigLocation location, @Nonnull String name, @Nonnull Type type,
                           @Nonnull Object obj, @Nullable Field field, @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            serializer.serialize(new SerializationInfo<>(type, null, field), location.getChild(name), obj);
        } else {
            location.setInSection(name, obj);
        }
    }

    private Object deserialize(@Nonnull ConfigLocation location, @Nonnull String name, @Nonnull Type type, @Nullable Field field,
                               @Nullable ConfigSerializer<Object> serializer) {
        if (serializer != null) {
            return serializer.deserialize(new SerializationInfo<>(type, null, field), location.getChild(name));
        } else {
            return location.getInSection(name);
        }
    }

    @Override
    public Map<?, ?> deserialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location) {
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

        ConfigurationSection children = location.getSection().getConfigurationSection(location.getName());
        if (children != null) {
            for (String index : children.getKeys(false)) {
                map.put(
                    //Deserialize key
                    deserialize(
                        location.getChild(String.valueOf(index)), "key", keyType, info.getField(), keySerializer),
                    //Deserialize value
                    deserialize(
                        location.getChild(String.valueOf(index)), "value", valueType, info.getField(), valueSerializer));
            }
        }
        return map;
    }
}
