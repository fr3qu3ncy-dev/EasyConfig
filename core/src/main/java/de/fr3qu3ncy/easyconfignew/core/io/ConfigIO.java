package de.fr3qu3ncy.easyconfignew.core.io;

import de.fr3qu3ncy.easyconfignew.core.EasyConfig;
import de.fr3qu3ncy.easyconfignew.core.configuration.ConfigField;
import de.fr3qu3ncy.easyconfignew.core.io.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfignew.core.io.config.ConfigDataWriter;
import de.fr3qu3ncy.easyconfignew.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfignew.core.utils.ConfigUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public final class ConfigIO {

    private final EasyConfig config;

    public <T> void writeToConfig(ConfigField<T> configField) {
        writeToConfig(configField.getPath(), configField.getDefaultValue(), configField);
    }

    public <T> void writeToConfig(String path, T value, ConfigField<T> configField) {
        //Check if value has a serializer
        ConfigSerializer<T> serializer = ConfigRegistry.getSerializer(configField.getFieldType());

        //Create new DataWriter
        DataWriter writer = new ConfigDataWriter(config.getFileConfig());

        //Save field information
        ConfigUtils.saveFieldInformation(writer, configField);

        //If type has a serializer, serialize it, otherwise set it directly
        if (serializer != null) {
            serializer.serialize(path, writer, value,
                new SerializationInfo<>(
                    config,
                    configField.getFieldType(),
                    configField.getDefaultValue(),
                    configField.getField()
                ));
        } else {
            writer.writeData(path, value);
        }

        //Save file after changes have been applied
        config.saveConfig();
    }

    @SneakyThrows
    public <T> void readToField(ConfigField<T> configField) {
        //Check if value has a serializer
        ConfigSerializer<T> serializer = ConfigRegistry.getSerializer(configField.getFieldType());

        //Create new DataSource
        DataSource source = new ConfigDataSource(config.getFileConfig());

        T deserializedObject;

        //If type has a serializer, deserialize it, otherwise read it directly from config
        if (serializer != null) {
            deserializedObject = serializer.deserialize(configField.getPath(), source,
                new SerializationInfo<>(
                    config,
                    configField.getFieldType(),
                    configField.getDefaultValue(),
                    configField.getField()
                ));
        } else {
            deserializedObject = config.getFileConfig().get(configField.getPath(), configField.getDefaultValue());
        }

        //Write deserialized object to field
        configField.getField().set(null, deserializedObject);
    }
}
