package de.fr3qu3ncy.easyconfignew.core.registry;

import de.fr3qu3ncy.easyconfignew.core.io.config.ConfigDataSource;
import de.fr3qu3ncy.easyconfignew.core.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfignew.core.serialization.Configurable;
import de.fr3qu3ncy.easyconfignew.core.serializers.CollectionSerializer;
import de.fr3qu3ncy.easyconfignew.core.serializers.EnumSerializer;
import de.fr3qu3ncy.easyconfignew.core.serializers.MapSerializer;
import de.fr3qu3ncy.easyconfignew.core.serializers.StringSerializer;
import de.fr3qu3ncy.easyconfignew.core.utils.ReflectionUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigRegistry {

    private ConfigRegistry() {}

    private static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();
    private static final List<Class<? extends ConfigSerializer<?>>> abstractConfigParsers = new ArrayList<>();
    private static final Map<Type, Function<ConfigDataSource, Class<?>>> variableTypes = new HashMap<>();

    static {
        register(String.class, new StringSerializer());
        register(Collection.class, new CollectionSerializer());
        register(Map.class, new MapSerializer());
        register(Enum.class, new EnumSerializer());
    }

    @SneakyThrows
    public static void register(Class<? extends Configurable<?>> clazz) {
        serializers.put(clazz, clazz.newInstance());
    }

    public static <T> void register(Class<T> clazz, ConfigSerializer<? extends T> serializer) {
        serializers.put(clazz, serializer);
    }

    public static void registerAbstract(Class<? extends ConfigSerializer<?>> clazz) {
        abstractConfigParsers.add(clazz);
    }

    public static void registerVariableType(Type type, Function<ConfigDataSource, Class<?>> func) {
        variableTypes.put(type, func);
    }

    public static Class<?> getVariableType(Type type, ConfigDataSource source) {
        if (!variableTypes.containsKey(type)) return null;

        Function<ConfigDataSource, Class<?>> func = variableTypes.get(type);
        return func.apply(source);
    }

    public static <T extends Type, P extends ConfigSerializer<?>> P getSerializer(T type) {
        if (type == null) return null;
        Class<?> typeClass = ReflectionUtils.typeToClass(type);

        //Load all possible parsers
        Map<Class<?>, ConfigSerializer<?>> possibleParsers = serializers.entrySet().stream()
            .filter(entry -> entry.getKey().equals(typeClass) || entry.getKey().isAssignableFrom(typeClass))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        abstractConfigParsers.stream()
            .filter(clazz -> clazz.isAssignableFrom(typeClass))
            .forEach(clazz -> possibleParsers.put(clazz, (ConfigSerializer<?>) ReflectionUtils.invokeEmptyConstructor(typeClass)));

        //Check if anyone of them exactly matches the type
        Optional<? extends ConfigSerializer<?>> exactMatch = possibleParsers.entrySet().stream()
            .filter(entry -> entry.getKey().equals(type))
            .map(Map.Entry::getValue)
            .findFirst();

        //Return exact match or first possible match
        return exactMatch.map(configParser -> (P) configParser)
            .orElseGet(() -> possibleParsers.isEmpty() ? null : (P) possibleParsers.entrySet().iterator().next().getValue());
    }
}
