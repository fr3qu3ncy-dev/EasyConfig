package de.fr3qu3ncy.easyconfig.core.serialization;

import de.fr3qu3ncy.easyconfig.core.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.data.DataSource;
import de.fr3qu3ncy.easyconfig.core.data.DataWriter;
import de.fr3qu3ncy.easyconfig.core.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.util.ReflectionUtils;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public interface Configurable<T> extends ConfigSerializer<T> {

    @SneakyThrows
    @Override
    default void serialize(@Nonnull SerializationInfo<?> info, DataWriter writer, @Nonnull T instance) {
        for (Field field : ReflectionUtils.getConfigurableFields(getClass()).keySet()) {

            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(instance);

            //Load serializer
            ConfigSerializer<Object> serializer = ConfigRegistry.getSerializer(value.getClass());

            //Check serializer
            if (serializer != null) {
                serializer.serialize(new SerializationInfo<>(info.config(), field.getGenericType(), null, field),
                    writer.getChildWriter(fieldName), value);
            } else {
                writer.writeData(fieldName, value);
            }
        }
    }

    @SneakyThrows
    @Override
    default T deserialize(@Nonnull SerializationInfo<?> info, DataSource source) {
        //Instantiate class from no args constructor
        Object instance;
        if (info.type() instanceof Class<?>) {
            instance = ((Class<?>) info.type()).newInstance();
        } else {
            instance = getClass().newInstance();
        }

        //Loop through all configurable fields
        ReflectionUtils.getConfigurableFields(getClass()).forEach((blankField, fieldType) -> {
            String fieldName = blankField.getName();

            Object deserializedObject;

            ConfigSerializer<?> serializer = ConfigRegistry.getSerializer(fieldType);
            if (serializer != null) {
                deserializedObject = serializer.deserialize(new SerializationInfo<>(info.config(), fieldType, null, blankField),
                    source.getChildSource(fieldName));
            } else {
                deserializedObject = source.getData(fieldName);
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
        if (value instanceof Double && ReflectionUtils.typeToClass(type).isAssignableFrom(float.class)) {
            return ((Double) value).floatValue();
        }
        return value;
    }
}
