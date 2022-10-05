package de.fr3qu3ncy.easyconfig.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public record SerializationInfo<T>(@Nonnull EasyConfig config, @Nonnull Type type, @Nullable T defaultValue,
                                   @Nullable Field field) {

}
