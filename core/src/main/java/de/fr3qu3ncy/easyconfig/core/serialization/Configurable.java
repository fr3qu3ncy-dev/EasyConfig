package de.fr3qu3ncy.easyconfig.core.serialization;

import de.fr3qu3ncy.easyconfig.core.annotations.CollectionKey;
import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface Configurable<T> extends ConfigSerializer<T> {

    @Override
    @SneakyThrows
    default void serialize(String path, DataWriter writer, @Nonnull T instance, SerializationInfo<? extends T> info) {
        for (Field field : ReflectionUtils.getConfigurableFields(getClass()).keySet()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(instance);
            Object defaultValue = field.get(getClass().newInstance());

            //Load serializer
            ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(field.getType());

            info.config().debug("Serializing " + value + " to field " + fieldName);
            if (value == null
                || (field.isAnnotationPresent(CollectionKey.class) && info.hasParent())) continue;

            //Check serializer
            serializer.serialize(path + "." + fieldName,
                writer, value, new SerializationInfo<>(info.config(), field.getGenericType(), defaultValue, field, false));
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

        info.config().debug("Deserializing object at path " + path + "...");

        //Loop through all configurable fields
        ReflectionUtils.getConfigurableFields(getClass()).forEach((blankField, fieldType) -> {
            String fieldName = blankField.getName();
            String subPath = path.isBlank() ? fieldName : path + "." + fieldName;

            ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(fieldType);
            Object deserializedObject = serializer.deserialize(subPath,
                source, new SerializationInfo<>(info.config(), fieldType, ReflectionUtils.getDefaultValue(blankField), blankField, false));

            if (deserializedObject == null && info.hasParent() && blankField.isAnnotationPresent(CollectionKey.class)) {
                String key = path.substring(path.lastIndexOf(".") + 1);
                try {
                    blankField.set(instance, key);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (deserializedObject == null) {
                Object defaultValue = ReflectionUtils.getDefaultValue(blankField);
                if (defaultValue != null) {
                    deserializedObject = defaultValue;
                } else {
                    info.config().debug("Skipping field " + blankField.getName() + " because no value was found.");
                    return;
                }
            }
            try {
                blankField.set(instance, checkDoubleValue(deserializedObject, fieldType));
                info.config().debug("Read value " + deserializedObject + " to field " + blankField.getName());
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
