package de.fr3qu3ncy.easyconfignew.core.serialization;

import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface Configurable<T> extends ConfigSerializer<T> {

    @Override
    @SneakyThrows
    default void serialize(String path, DataWriter writer, @Nonnull T instance, SerializationInfo<T> info) {
        for (Field field : ReflectionUtils.getConfigurableFields(getClass()).keySet()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(instance);

            //Load serializer
            ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(value.getClass());

            //Check serializer
            if (serializer != null) {
                serializer.serialize(path + "." + fieldName,
                    writer, value, new SerializationInfo<>(info.config(), field.getGenericType(), null, field));
            } else {
                writer.writeData(path + "." + fieldName, value);
            }
        }
    }

    @Override
    @SneakyThrows
    default T deserialize(String path, DataSource source, SerializationInfo<T> info) {
        //Instantiate class from no args constructor
        Object instance;
        if (info.type() instanceof Class<?>) {
            instance = ((Class<?>) info.type()).newInstance();
        } else {
            instance = getClass().newInstance();
        }

        info.config().getLogger().info("Deserializing object at path " + path + "...");

        //Loop through all configurable fields
        ReflectionUtils.getConfigurableFields(getClass()).forEach((blankField, fieldType) -> {
            String fieldName = blankField.getName();

            Object deserializedObject;

            ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(fieldType);
            if (serializer != null) {
                deserializedObject = serializer.deserialize(path + "." + fieldName,
                    source, new SerializationInfo<>(info.config(), fieldType, null, blankField));
            } else {
                deserializedObject = source.getData(path + "." + fieldName);
            }

            try {
                blankField.set(instance, checkDoubleValue(deserializedObject, fieldType));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return (T) instance;
    }

    static Object checkDoubleValue(Object value, Type type) {
        if (value instanceof Double doubleValue && ReflectionUtils.typeToClass(type).isAssignableFrom(float.class)) {
            return doubleValue.floatValue();
        }
        return value;
    }
}
