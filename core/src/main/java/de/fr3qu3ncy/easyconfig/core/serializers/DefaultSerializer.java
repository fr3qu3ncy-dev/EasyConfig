package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.utils.SerializationUtils;

import javax.annotation.Nullable;

public class DefaultSerializer implements ConfigSerializer<Object> {

    @Override
    public void serialize(String path, DataWriter writer, @Nullable Object value, SerializationInfo<?> info) {
        if (!SerializationUtils.checkDefaultValue(path, writer, value, info)) return;
        writer.writeData(path, value);
    }

    @Override
    public Object deserialize(String path, DataSource source, SerializationInfo<Object> info) {
        Object value = source.getData(path);
        return value != null ? value : info.defaultValue();
    }
}
