package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.io.list.ListDataSource;
import de.fr3qu3ncy.easyconfig.core.io.map.MapDataSource;
import de.fr3qu3ncy.easyconfig.core.io.map.MapDataWriter;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionSerializer implements ConfigSerializer<Collection<?>> {

    @Override
    public Collection<?> deserialize(String path, DataSource source, @Nonnull SerializationInfo<Collection<?>> info) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) ReflectionUtils.typeToClass(info.type()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            collection = new ArrayList<>();
        }

        Type collectionType = ReflectionUtils.getGenericTypes(info.type(), 1, "Unknown collection type!")
            .get(0);

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

        //Check if there is a ConfigSection at this path, if yes, there are children,
        //if no, there is a homogenous list
        collection.addAll(loadListFromChildren(path, serializer, source, collectionType, info));
        return collection;
    }

    private Collection<Object> loadListFromChildren(String path, ConfigSerializer<?> serializer, DataSource source,
                                                    Type collectionType, SerializationInfo<Collection<?>> info) {
        Collection<Object> collection = new ArrayList<>();

        try {
            //Lists are stored as lists of key-value pairs, so load them and add them to the collection
            List<LinkedHashMap<String, Object>> list = source.getData(path);

            //If list is non-existent just return empty one
            if (list == null) return collection;

            for (LinkedHashMap<String, Object> element : list) {
                collection.add(serializer.deserialize("", new MapDataSource(element),
                    new SerializationInfo<>(info.config(), collectionType, null, info.field(), true)));
            }
        } catch (ClassCastException ex) {
            //Check if type has a collection key
            info.config().debug("[CollectionSerializer] Checking if collection has key for type " + collectionType + "...");
            String keyFieldName = ReflectionUtils.getCollectionKey((Class<?>) collectionType);
            if (keyFieldName != null) {
                source.getKeys(path).forEach(key -> {
                    Object obj = serializer.deserialize(path + "." + key, source,
                        new SerializationInfo<>(info.config(), collectionType, null, info.field(), true));
                    collection.add(obj);
                });
                return collection;
            }

            //Collection is not saved as key-value pairs
            List<Object> list = source.getData(path);

            DataSource listSource = new ListDataSource(list);
            for (int i = 0 ; i < list.size() ; i++) {
                collection.add(serializer.deserialize(String.valueOf(i), listSource,
                    new SerializationInfo<>(info.config(), collectionType, null, info.field(), false)));
            }
        }
        return collection;
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nullable Collection<?> collection, SerializationInfo<? extends Collection<?>> info) {
        Type collectionType = ReflectionUtils.getGenericTypes(info.type(), 1,
            "Unknown collection type!").get(0);

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

        //Don't even bother if collection is empty and just write an empty ArrayList
        if (collection == null || collection.isEmpty()) {
            if (info.defaultValue() == null || collection == null || collection.equals(info.defaultValue())) return;
            info.config().debug("Serializing empty list...");
            writer.writeData(path, new ArrayList<>());
            return;
        }

        //Check if type has a collection key
        info.config().debug("[CollectionSerializer] Checking if collection has key for type " + collectionType + "...");
        String keyFieldName = ReflectionUtils.getCollectionKey((Class<?>) collectionType);
        Field keyField = null;
        if (keyFieldName != null) {
            try {
                keyField = ((Class<?>) collectionType).getDeclaredField(keyFieldName);
                keyField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        info.config().debug("[CollectionSerializer] Collection key is " + keyFieldName);

        List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
        Field finalKeyField = keyField;
        collection.stream().filter(Objects::nonNull)
            .forEach(obj -> {
                info.config().debug("[CollectionSerializer] Serializing object " + obj + " to serializer " + serializer);

                if (finalKeyField != null) {
                    //If yes, serialize it to the path + collectionKey
                    serializeWithKey(path, writer, info, collectionType, serializer, finalKeyField, obj);
                    return;
                }

                //If not, serialize it to a map and add it to the list
                MapDataWriter mapWriter = new MapDataWriter(new LinkedHashMap<>());

                serializer.serialize("", mapWriter, obj,
                    new SerializationInfo<>(info.config(), collectionType, null, info.field(), false));

                mapList.add(mapWriter.getMap());
            });
        //Check if type has no keys
        if (keyField == null && mapList.stream().allMatch(map -> map.keySet().stream().allMatch(String::isEmpty))) {
            serializeMapValues(path, writer, info, mapList);
            return;
        }
        if (keyField == null) writer.writeData(path, mapList);
    }

    private static void serializeWithKey(String path, DataWriter writer, SerializationInfo<? extends Collection<?>> info,
                                         Type collectionType, ConfigSerializer<Object> serializer,
                                         Field finalKeyField, Object obj) {
        try {
            String collectionKey = finalKeyField.get(obj).toString();
            serializer.serialize(path + "." + collectionKey, writer, obj,
                new SerializationInfo<>(info.config(), collectionType, null, info.field(), true));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void serializeMapValues(String path, DataWriter writer, SerializationInfo<? extends Collection<?>> info, List<LinkedHashMap<String, Object>> mapList) {
        info.config().debug("[CollectionSerializer] Collection had no keys.");

        List<Object> valueList = new ArrayList<>();
        mapList.forEach(map -> valueList.addAll(map.values()));
        writer.writeData(path, valueList);
    }
}
