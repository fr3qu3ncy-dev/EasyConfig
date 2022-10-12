package de.fr3qu3ncy.easyconfignew.core.configuration;

import de.fr3qu3ncy.easyconfignew.core.annotations.Comment;
import de.fr3qu3ncy.easyconfignew.core.annotations.ConfigGroup;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Getter
public class ConfigField<T> {

    private final Field field;
    @Nullable
    private final T defaultValue;
    private final String path;
    private final Type fieldType;

    /* Additional Field Information */
    private String comment;
    private String group;
    private boolean writeGroupHeader;

    @SneakyThrows
    public ConfigField(Field field) {
        this.field = field;
        this.defaultValue = (T) field.get(null);
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
}
