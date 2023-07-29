package de.fr3qu3ncy.easyconfig.core.utils;

import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class SerializationUtils {

    private SerializationUtils() {
    }

    public static boolean checkDefaultValue(String path, DataWriter writer, @Nullable Object value, SerializationInfo<?> info) {
        Field field = info.field();
        if ((value == null || value.equals(info.defaultValue()))
            && (field != null && ReflectionUtils.isOptional(field.getDeclaringClass()))) {
            writer.writeData(path, null);
            return false;
        }
        return true;
    }
}
