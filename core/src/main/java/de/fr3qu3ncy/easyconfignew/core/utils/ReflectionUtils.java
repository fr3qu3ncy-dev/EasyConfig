package de.fr3qu3ncy.easyconfignew.core.utils;

import de.fr3qu3ncy.easyconfignew.core.annotations.ConfigPath;
import de.fr3qu3ncy.easyconfignew.core.annotations.ConfigurableField;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.*;

public class ReflectionUtils {

    private ReflectionUtils() {}

    public static boolean isValidConfigurableField(Field field) {
        //Field needs to be public static and be at least annotated with ConfigPath
        return field.isAnnotationPresent(ConfigPath.class) && Modifier.isStatic(field.getModifiers());
    }

    public static String getPath(Field field) {
        if (!field.isAnnotationPresent(ConfigPath.class)) return null;
        return field.getAnnotation(ConfigPath.class).value();
    }

    public static List<Type> getGenericTypes(Type type, int count, String errorMessage) {
        List<Type> collectionTypes = getGenericTypes(type);
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
        if (type instanceof ParameterizedType parameterizedType) {
            return Arrays.stream(parameterizedType.getActualTypeArguments()).toList();
        }
        return new ArrayList<>();
    }

    public static Class<?> typeToClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof GenericArrayType genericArrayType) {
            // having to create an array instance to get the class is kinda nasty
            // but apparently this is a current limitation of java-reflection concerning array classes.
            return Array.newInstance(typeToClass(genericArrayType.getGenericComponentType()), 0).getClass(); // E.g. T[] -> T -> Object.class if <T> or Number.class if <T extends Number & Comparable>
        } else if (type instanceof ParameterizedType parameterizedType) {
            return typeToClass(parameterizedType.getRawType()); // Eg. List<T> would return List.class
        } else if (type instanceof TypeVariable<?> typeVariable) {
            Type[] bounds = typeVariable.getBounds();
            return bounds.length == 0 ? Object.class : typeToClass(bounds[0]); // erasure is to the left-most bound.
        } else if (type instanceof WildcardType wildcardType) {
            Type[] bounds = wildcardType.getUpperBounds();
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
                    field.isAnnotationPresent(ConfigurableField.class) || field.getDeclaringClass().isAnnotationPresent(ConfigurableField.class))
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
}
