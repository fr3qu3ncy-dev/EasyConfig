package de.fr3qu3ncy.easyconfig.serializers;

import de.fr3qu3ncy.easyconfig.SerializationInfo;
import de.fr3qu3ncy.easyconfig.annotation.VariableType;
import de.fr3qu3ncy.easyconfig.data.DataSource;
import de.fr3qu3ncy.easyconfig.data.DataWriter;
import de.fr3qu3ncy.easyconfig.data.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.data.map.MapDataSource;
import de.fr3qu3ncy.easyconfig.data.map.MapDataWriter;
import de.fr3qu3ncy.easyconfig.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.util.ReflectionUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class CollectionSerializer implements ConfigSerializer<Collection<?>> {

    @SneakyThrows
    @Override
    public void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull Collection<?> collection) {
        Type collectionType = ReflectionUtils.getGenericTypes(info.getType(), 1,
            "Unknown collection type!").get(0);

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);

        //Check if field has a key annotation
        String keyFieldName = ReflectionUtils.getCollectionKeyField(info.getField());

        //Class has serializer
        if (serializer != null) {
            if (collection.isEmpty()) {
                writer.writeData(new ArrayList<>());
                return;
            }
            List<LinkedHashMap<Object, Object>> mapList = new ArrayList<>();
            for (Object obj : collection) {
                if (obj == null) continue;

                String key = ReflectionUtils.getCollectionKey(keyFieldName, obj);

                MapDataWriter mapWriter = new MapDataWriter(writer.getKey(), new LinkedHashMap<>());

                serializer.serialize(new SerializationInfo<>(collectionType, null, info.getField()),
                    key != null ? mapWriter.getChildWriter(key) : mapWriter, obj);
                mapList.add(mapWriter.getMap());
            }
            writer.writeData(mapList);
            return;
        }
        //Class doesn't have serializer
        writer.writeData(collection);
    }

    @Override
    public Collection<?> deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) ReflectionUtils.typeToClass(info.getType()).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            collection = new ArrayList<>();
        }

        Type collectionType = ReflectionUtils.getGenericTypes(info.getType(), 1, "Unknown collection type!")
            .get(0);
        if (info.getField() != null && info.getField().isAnnotationPresent(VariableType.class)) {
            collectionType = ConfigRegistry.getVariableType(collectionType, (ConfigDataSource) source);
        }

        //Check (existing) serializer
        ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(collectionType);
        if (serializer != null) {
            System.out.println("Deserializing key " + source.getKey());
            List<LinkedHashMap<Object, Object>> list = source.getData();
            for (LinkedHashMap<Object, Object> element : list) {
                collection.add(serializer.deserialize(info, new MapDataSource(null, element)));
            }
        } else {
            collection.addAll(source.getData());
        }
        return collection;
    }
}
