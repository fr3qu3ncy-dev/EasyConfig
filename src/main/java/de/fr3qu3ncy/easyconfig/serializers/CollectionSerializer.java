package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.ConfigLocation;
import de.fr3qu3ncy.easyconfig.annotation.CollectionKey;
import de.fr3qu3ncy.easyconfig.annotation.VariableType;
import de.fr3qu3ncy.easyconfig.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.util.ReflectionUtils;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionSerializer implements ConfigSerializer<Collection<?>> {

    @SneakyThrows
    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location, @Nonnull Collection<?> collection) {
        Type collectionType = ReflectionUtils.getGenericTypes(info.getType(), 1,
            "Unknown collection type!").get(0);

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

        //Check if field has a key annotation
        String keyFieldName = null;
        if (info.getField() != null && info.getField().isAnnotationPresent(CollectionKey.class)) {
            keyFieldName = info.getField().getAnnotation(CollectionKey.class).value();
        }

        //Class has serializer
        if (serializer != null) {
            if (collection.isEmpty()) {
                location.setSingle(new ArrayList<>());
            } else {
                int counter = 0;
                for (Object obj : collection) {

                    String key = null;
                    //Use custom key if available
                    if (keyFieldName != null) {
                        Field field = ReflectionUtils.findField(keyFieldName, obj.getClass());
                        field.setAccessible(true);

                        Object keyObject = field.get(obj);
                        if (keyObject instanceof String) {
                            key = (String) keyObject;
                        } else if (keyObject instanceof Enum<?>) {
                            key = ((Enum<?>) keyObject).name();
                        }
                    } else {
                        key = String.valueOf(counter);
                    }


                    if (obj != null) {
                        serializer.serialize(
                            new SerializationInfo<>(collectionType, null, info.getField()),
                            location.getChild(key),
                            obj);
                    }
                    counter++;
                }
            }
        } else {
            //Class doesn't have serializer
            location.getSection().set(location.getName(), collection);
        }
    }

    @Override
    public Collection<?> deserialize(@Nonnull SerializationInfo<?> info, @Nonnull ConfigLocation location) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) ReflectionUtils.typeToClass(info.getType()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            collection = new ArrayList<>();
        }

        ConfigurationSection children = location.getSection().getConfigurationSection(location.getName());
        if (children != null) {
            for (String key : children.getKeys(false)) {

                ConfigLocation childLocation = location.getChild(key);

                Type collectionType = ReflectionUtils.getGenericTypes(info.getType(), 1, "Unknown collection type!")
                    .get(0);
                if (info.getField() != null && info.getField().isAnnotationPresent(VariableType.class)) {
                    collectionType = ConfigRegistry.getVariableType(collectionType, childLocation);
                }

                //Check (existing) serializer
                ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

                if (serializer != null) {
                    collection.add(serializer.deserialize(new SerializationInfo<>(collectionType, null, info.getField()),
                        childLocation));
                } else {
                    addList(collection, location);
                }
            }
        } else {
            addList(collection, location);
        }
        return collection;
    }

    private void addList(Collection<Object> collection, ConfigLocation location) {
        List<?> list = location.getSingle();
        if (list != null) {
            collection.addAll(list);
        }
    }
}
