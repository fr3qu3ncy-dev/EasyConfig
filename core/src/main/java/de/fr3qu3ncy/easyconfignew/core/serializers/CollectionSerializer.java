package de.fr3qu3ncy.easyconfignew.core.serializers;

import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.io.map.MapDataSource;
import de.fr3qu3ncy.easyconfignew.core.io.map.MapDataWriter;
import de.fr3qu3ncy.easyconfignew.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

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

        if (serializer != null) {
            //Check if there is a ConfigSection at this path, if yes, there are children,
            //if no, there is a homogenous list
            if (info.config().getFileConfig().getChild(path) != null) {
                //Here we have a key-value collection, so load it
                collection.addAll(loadListFromChildren(path, serializer, source, collectionType, info));
            } else {
                //List was homogenous
                collection.addAll(source.getData(path));
            }
        } else {
            //Collection Type had no serializer at all
            collection.addAll(source.getData(path));
        }
        return collection;
    }

    private Collection<Object> loadListFromChildren(String path, ConfigSerializer<?> serializer, DataSource source,
                                                    Type collectionType, SerializationInfo<Collection<?>> info) {
        Collection<Object> collection = new ArrayList<>();

        //Lists are stored as lists of key-value pairs, so load them and add them to the collection
        List<LinkedHashMap<String, Object>> list = source.getData(path);
        for (LinkedHashMap<String, Object> element : list) {
            collection.add(serializer.deserialize("", new MapDataSource(element),
                new SerializationInfo<>(info.config(), collectionType, null, info.field())));
        }
        return collection;
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull Collection<?> collection, SerializationInfo<Collection<?>> info) {
        Type collectionType = ReflectionUtils.getGenericTypes(info.type(), 1,
            "Unknown collection type!").get(0);

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

        //Class has serializer
        if (serializer != null) {
            if (collection.isEmpty()) {
                writer.writeData(path, new ArrayList<>());
                return;
            }
            List<LinkedHashMap<String, Object>> mapList = new ArrayList<>();
            for (Object obj : collection) {
                if (obj == null) continue;

                MapDataWriter mapWriter = new MapDataWriter(new LinkedHashMap<>());

                serializer.serialize("", mapWriter, obj,
                    new SerializationInfo<>(info.config(), collectionType, null, info.field()));

                mapList.add(mapWriter.getMap());
            }
            //Check if type has no keys
            if (mapList.stream().allMatch(map -> map.keySet().stream().allMatch(String::isEmpty))) {
                List<Object> valueList = new ArrayList<>();
                mapList.forEach(map -> valueList.addAll(map.values()));
                writer.writeData(path, valueList);
                return;
            }
            writer.writeData(path, mapList);
            return;
        }
        //Class doesn't have serializer
        writer.writeData(path, collection);
    }
}
