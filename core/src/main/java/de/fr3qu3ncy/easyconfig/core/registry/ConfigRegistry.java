package de.fr3qu3ncy.easyconfig.core.registry;

import de.fr3qu3ncy.easyconfig.core.serialization.Configurable;
import de.fr3qu3ncy.easyconfig.core.serializers.*;
import de.fr3qu3ncy.easyconfig.core.utils.ReflectionUtils;
import de.fr3qu3ncy.easyconfig.core.io.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfig.core.serialization.ConfigSerializer;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigRegistry {

    private ConfigRegistry() {
    }

    private static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();
    private static final Map<Class<?>, ConfigSerializer<?>> abstractConfigParsers = new HashMap<>();
    private static final Map<Type, Function<ConfigDataSource, Class<?>>> variableTypes = new HashMap<>();

    private static final ConfigSerializer<?> DEFAULT_SERIALIZER = new DefaultSerializer();

    static {
        register(String.class, new StringSerializer());
        register(Collection.class, new CollectionSerializer());
        register(Map.class, new MapSerializer());
        register(Enum.class, new EnumSerializer());
    }

    public static <T> ConfigSerializer<T> getDefaultSerializer() {
        return (ConfigSerializer<T>) DEFAULT_SERIALIZER;
    }

    @SneakyThrows
    public static void register(Class<? extends Configurable<?>> clazz) {
        serializers.put(clazz, clazz.newInstance());
    }

    public static <T> void register(Class<T> clazz, ConfigSerializer<? extends T> serializer) {
        serializers.put(clazz, serializer);
    }

    public static void registerAbstract(Class<?> clazz, ConfigSerializer<?> serializer) {
        abstractConfigParsers.put(clazz, serializer);
    }

    public static void registerVariableType(Type type, Function<ConfigDataSource, Class<?>> func) {
        variableTypes.put(type, func);
    }

    public static Class<?> getVariableType(Type type, ConfigDataSource source) {
        if (!variableTypes.containsKey(type)) return null;

        Function<ConfigDataSource, Class<?>> func = variableTypes.get(type);
        return func.apply(source);
    }

    @Nonnull
    public static <T extends Type, P extends ConfigSerializer<?>> P getSerializer(T type) {
        if (type == null) return (P) DEFAULT_SERIALIZER;
        Class<?> typeClass = ReflectionUtils.typeToClass(type);

        //Load all possible parsers
        Map<Class<?>, ConfigSerializer<?>> possibleParsers = serializers.entrySet().stream()
            .filter(entry -> entry.getKey().equals(typeClass) || entry.getKey().isAssignableFrom(typeClass))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        abstractConfigParsers.entrySet().stream()
            .filter(entry -> entry.getKey().isAssignableFrom(typeClass))
            .forEach(entry -> possibleParsers.put(entry.getKey(), entry.getValue()));

        //Check if anyone of them exactly matches the type
        Optional<? extends ConfigSerializer<?>> exactMatch = possibleParsers.entrySet().stream()
            .filter(entry -> entry.getKey().equals(type))
            .map(Map.Entry::getValue)
            .findFirst();

        //Return exact match or first possible match
        return exactMatch.map(configParser -> (P) configParser)
            .orElseGet(() -> possibleParsers.isEmpty() ? (P) DEFAULT_SERIALIZER : (P) possibleParsers.entrySet().iterator().next().getValue());
    }
}
