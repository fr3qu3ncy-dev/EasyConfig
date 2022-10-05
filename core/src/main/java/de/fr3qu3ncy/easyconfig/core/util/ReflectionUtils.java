package de.fr3qu3ncy.easyconfig.core.util;

import de.fr3qu3ncy.easyconfig.core.annotation.CollectionKey;
import de.fr3qu3ncy.easyconfig.core.annotation.ConfigIgnore;
import de.fr3qu3ncy.easyconfig.core.annotation.ConfigurableField;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static List<Type> getGenericTypes(Type type, int count, String errorMessage) {
        List<Type> collectionTypes = ReflectionUtils.getGenericTypes(type);
        if (collectionTypes.size() < count) {
            throw new IllegalArgumentException(errorMessage);
        }
        List<Type> returnCollectionTypes = new ArrayList<>();
        for (int i = 0 ; i < count ; i++) {
            returnCollectionTypes.add(collectionTypes.get(i));
        }
        return returnCollectionTypes;
    }

    public static List<Type> getGenericTypes(Type type) {
        if (type instanceof ParameterizedType) {
            return Arrays.stream(((ParameterizedType) type).getActualTypeArguments()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static Class<?> typeToClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType) {
            // having to create an array instance to get the class is kinda nasty
            // but apparently this is a current limitation of java-reflection concerning array classes.
            return Array.newInstance(typeToClass(((GenericArrayType)type).getGenericComponentType()), 0).getClass(); // E.g. T[] -> T -> Object.class if <T> or Number.class if <T extends Number & Comparable>
        } else if (type instanceof ParameterizedType) {
            return typeToClass(((ParameterizedType) type).getRawType()); // Eg. List<T> would return List.class
        } else if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length == 0 ? Object.class : typeToClass(bounds[0]); // erasure is to the left-most bound.
        } else if (type instanceof WildcardType) {
            Type[] bounds = ((WildcardType) type).getUpperBounds();
            return bounds.length == 0 ? Object.class : typeToClass(bounds[0]); // erasure is to the left-most upper bound.
        } else {
            throw new UnsupportedOperationException("Cannot handle type class: " + type.getClass());
        }
    }

    public static Map<Field, Type> getConfigurableFields(Type type) {
        Map<Integer, List<Field>> fieldLevels = new TreeMap<>(Comparator.reverseOrder());
        int i = 0;
        for (Class<?> c = (Class<?>) type; c != null; c = c.getSuperclass()) {
            int finalI = i;
            Arrays.stream(c.getDeclaredFields())
                .filter(field ->
                    (!field.isAnnotationPresent(ConfigIgnore.class) && !field.getDeclaringClass().isAnnotationPresent(ConfigIgnore.class))
                    && (field.isAnnotationPresent(ConfigurableField.class) || field.getDeclaringClass().isAnnotationPresent(ConfigurableField.class)))
                .forEach(field -> {
                    List<Field> levelFields = fieldLevels.getOrDefault(finalI, new LinkedList<>());
                    levelFields.add(field);
                    fieldLevels.put(finalI, levelFields);

                    field.setAccessible(true);
                });
            i++;
        }

        Map<Field, Type> fields = new LinkedHashMap<>();
        fieldLevels.forEach((level, fieldList) -> fieldList.forEach(field -> fields.put(field, field.getGenericType())));

        return fields;
    }

    @SneakyThrows
    public static Field findField(String name, Class<?> clazz) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null) {
                return findField(name, clazz.getSuperclass());
            } else {
                return null;
            }
        }
    }

    @SneakyThrows
    public static Object invokeEmptyConstructor(Class<?> clazz) {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static String getCollectionKeyField(Field field) {
        if (field != null && field.isAnnotationPresent(CollectionKey.class)) {
            return field.getAnnotation(CollectionKey.class).value();
        }
        return null;
    }

    @SneakyThrows
    public static String getCollectionKey(String keyFieldName, Object obj) {
        String key = null;
        //Use custom key if available
        if (keyFieldName != null) {
            Field field = ReflectionUtils.findField(keyFieldName, obj.getClass());
            field.setAccessible(true);

            Object keyObject = field.get(obj);
            if (keyObject instanceof String) {
                key = (String) keyObject;
            } else if (keyObject instanceof Enum<?>) {
                key = ((Enum<?>) keyObject).name();
            }
        }
        return key;
    }
}
