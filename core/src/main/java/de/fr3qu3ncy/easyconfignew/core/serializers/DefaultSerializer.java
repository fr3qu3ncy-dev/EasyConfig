package de.fr3qu3ncy.easyconfignew.core.serializers;

import de.fr3qu3ncy.easyconfignew.core.io.DataSource;
import de.fr3qu3ncy.easyconfignew.core.io.DataWriter;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;

import javax.annotation.Nonnull;

public class DefaultSerializer implements ConfigSerializer<Object> {

    @Override
    public void serialize(String path, DataWriter writer, @Nonnull Object value, SerializationInfo<Object> info) {
        writer.writeData(path, value);
    }

    @Override
    public Object deserialize(String path, DataSource source, SerializationInfo<Object> info) {
        return source.getData(path);
    }
}
