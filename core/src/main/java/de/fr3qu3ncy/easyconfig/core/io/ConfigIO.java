package de.fr3qu3ncy.easyconfig.core.io;

import de.fr3qu3ncy.easyconfig.core.configuration.ConfigField;
import de.fr3qu3ncy.easyconfig.core.registry.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.core.serialization.SerializationInfo;
import de.fr3qu3ncy.easyconfig.core.EasyConfig;
import de.fr3qu3ncy.easyconfig.core.io.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.core.io.config.ConfigDataWriter;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.core.utils.ConfigUtils;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public final class ConfigIO {

    private final EasyConfig config;

    public <T> void writeToConfig(ConfigField<T> configField) {
        writeToConfig(configField.getPath(), configField.getDefaultValue(), configField);
    }

    public <T> void writeToConfig(String path, T value, ConfigField<T> configField) {
        //Check if value has a serializer
        ConfigSerializer<T> serializer = ConfigRegistry.getSerializer(configField.getFieldType());

        config.debug("Writing value " + value + " to path " + path);

        //Create new DataWriter
        DataWriter writer = new ConfigDataWriter(config.getFileConfig());

        //Save field information
        ConfigUtils.saveFieldInformation(writer, configField);

        //If type has a serializer, serialize it, otherwise set it directly
        serializer.serialize(path, writer, value,
            new SerializationInfo<>(
                config,
                configField.getFieldType(),
                configField.getDefaultValue(),
                configField.getField(),
                false
            ));

        //Save file after changes have been applied
        config.saveConfig();
    }

    public <T> void readToField(ConfigField<T> configField) {
        //Check if value has a serializer
        ConfigSerializer<T> serializer = ConfigRegistry.getSerializer(configField.getFieldType());

        //Create new DataSource
        DataSource source = new ConfigDataSource(config.getFileConfig());

        T deserializedObject;

        //If type has a serializer, deserialize it, otherwise read it directly from config
        deserializedObject = serializer.deserialize(configField.getPath(), source,
            new SerializationInfo<>(
                config,
                configField.getFieldType(),
                configField.getDefaultValue(),
                configField.getField(),
                false
            ));

        //Write deserialized object to field
        set(configField, deserializedObject);
    }

    public <T> void set(String path, T value) {
        ConfigField<T> configField = ConfigField.fromPath(path, Arrays.asList(config.getFields()), config.getBlankDefaultInstance());
        if (configField == null) {
            config.debug("Tried to set null path " + path + "!");
            return;
        }
        writeToConfig(path, value, configField);
    }

    private void set(ConfigField<?> configField, Object value) {
        Object instance = config.getHolderInstance();
        try {
            configField.getField().set(instance, value);
            config.debug("Read value " + value + " to field " + configField.getField().getName() + " in instance " + instance);
        } catch (IllegalAccessException e) {
            config.debug("Error while reading field " + configField.getField().getName());
            e.printStackTrace();
        }
    }
}
