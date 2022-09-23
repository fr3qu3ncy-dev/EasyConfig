package de.fr3qu3ncy.easyconfig.register;

import com.mojang.datafixers.types.Func;
import de.fr3qu3ncy.easyconfig.ConfigLocation;
import de.fr3qu3ncy.easyconfig.serialization.ConfigSerializer;
import de.fr3qu3ncy.easyconfig.serialization.Configurable;
import de.fr3qu3ncy.easyconfig.serializers.CollectionSerializer;
import de.fr3qu3ncy.easyconfig.serializers.EnumSerializer;
import de.fr3qu3ncy.easyconfig.serializers.MapSerializer;
import de.fr3qu3ncy.easyconfig.serializers.StringSerializer;
import de.fr3qu3ncy.easyconfig.util.ReflectionUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigRegistry {

    private ConfigRegistry() {}

    private static final Map<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();
    private static final List<Class<? extends ConfigSerializer<?>>> abstractConfigParsers = new ArrayList<>();
    private static final Map<Type, Function<ConfigLocation, Class<?>>> variableTypes = new HashMap<>();

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

    public static void registerVariableType(Type type, Function<ConfigLocation, Class<?>> func) {
        variableTypes.put(type, func);
    }

    public static Class<?> getVariableType(Type type, ConfigLocation location) {
        if (!variableTypes.containsKey(type)) return null;

        Function<ConfigLocation, Class<?>> func = variableTypes.get(type);
        return func.apply(location);
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