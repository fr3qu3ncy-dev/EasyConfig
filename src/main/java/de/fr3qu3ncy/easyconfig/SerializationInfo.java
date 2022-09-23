package de.fr3qu3ncy.easyconfig;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

@Getter
@AllArgsConstructor
public class SerializationInfo<T> {

    @Nonnull
    private final Type type;

    @Nullable
    private final T defaultValue;

    @Nullable
    private final Field field;
}
