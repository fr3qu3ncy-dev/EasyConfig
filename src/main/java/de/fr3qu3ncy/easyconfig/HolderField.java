package de.fr3qu3ncy.easyconfig;

import de.fr3qu3ncy.easyconfig.annotation.Comment;
import de.fr3qu3ncy.easyconfig.annotation.ConfigPath;
import de.fr3qu3ncy.easyconfig.register.ConfigRegistry;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Getter
public class HolderField {

    private final Field field;
    private final EasyConfig config;
    private final Type fieldType;
    private final Class<?> fieldClass;

    private final String path;
    private final Object defaultValue;
    private final ConfigSerializer<Object> parser;
    private String comment;

    @SneakyThrows
    public HolderField(EasyConfig config, Field field) {
        ConfigPath data = field.getAnnotation(ConfigPath.class);

        this.field = field;
        this.fieldType = field.getGenericType();
        this.fieldClass = field.getType();
        this.config = config;
        this.path = data.value();

        //Get the specified default value
        this.defaultValue = field.get(null);

        config.logInfo("Loaded field " + field.getName() + ":");
        config.logInfo("    - Type: " + fieldType.getTypeName());
        config.logInfo("    - Field Class: " + fieldClass.getName());
        config.logInfo("    - Path: " + path);
        config.logInfo("    - Default: " + defaultValue);

        //Check if there is a parser for specified type
        Class<?> type = field.getType();
        this.parser = ConfigRegistry.getSerializer(type);

        if (field.isAnnotationPresent(Comment.class)) {
            this.comment = field.getAnnotation(Comment.class).value();
        }
    }

}
