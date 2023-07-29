package de.fr3qu3ncy.easyconfig.core.configuration;

import de.fr3qu3ncy.easyconfig.core.annotations.Comment;
import de.fr3qu3ncy.easyconfig.core.annotations.ConfigGroup;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;

@SuppressWarnings("unchecked")
@Getter
public class ConfigField<T> {

    private final Field field;
    @Nullable
    private final T defaultValue;
    @Nullable
    private final String path;
    private final Type fieldType;

    /* Additional Field Information */
    private String comment;
    private String group;
    private boolean writeGroupHeader;

    @SneakyThrows
    public ConfigField(Field field, Object blankInstance) {
        this.field = field;
        field.setAccessible(true);

        this.defaultValue = (T) field.get(blankInstance);
        this.path = ReflectionUtils.getPath(field);
        this.fieldType = field.getGenericType();

        if (field.isAnnotationPresent(Comment.class)) {
            this.comment = field.getAnnotation(Comment.class).value();
        }
        if (field.isAnnotationPresent(ConfigGroup.class)) {
            ConfigGroup configGroup = field.getAnnotation(ConfigGroup.class);
            this.group = configGroup.value();
            this.writeGroupHeader = configGroup.writeHeader();
        }
    }

    public static <T> ConfigField<T> fromPath(String path, Collection<Field> fields, Object instance) {
        return fields.stream().filter(f -> path.equalsIgnoreCase(ReflectionUtils.getPath(f)))
            .map(f -> new ConfigField<T>(f, instance))
            .findFirst()
            .orElse(null);
    }
}
