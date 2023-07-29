package de.fr3qu3ncy.easyconfig.core.serializers;

import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.io.DataSource;
import de.fr3qu3ncy.easyconfig.core.io.DataWriter;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;

import javax.annotation.Nullable;

public class StringSerializer implements ConfigSerializer<String> {

    private static String formatColors(EasyConfig config, String message) {
        if (message == null) return null;
        return config.getStringFormatter().formatString(message);
    }

    @Override
    public void serialize(String path, DataWriter writer, @Nullable String value, SerializationInfo<? extends String> info) {
        ConfigSerializer<String> serializer = ConfigRegistry.getDefaultSerializer();
        serializer.serialize(path, writer, value, info);
    }

    @Override
    public String deserialize(String path, DataSource source, SerializationInfo<String> info) {
        return formatColors(info.config(), source.getData(path));
    }
}
