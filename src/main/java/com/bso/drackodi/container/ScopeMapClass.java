package com.bso.drackodi.container;

import com.bso.drackodi.scope.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ScopeMapClass {

    private final Map<Class<?>, Set<Object>> map = new HashMap<>();
    private final Scope scope;

    public ScopeMapClass(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean contains(Class<?> clazz) {
        return map.containsKey(clazz);
    }

    public synchronized <T> void putIfAbsent(Class<T> key, Class<?> implementationClass) {
        if (contains(key)) {
            Set<Object> objects = map.get(key);
            objects.add(createObject(implementationClass));
            return;
        }

        Set<Object> set = new HashSet<>();
        set.add(createObject(implementationClass));
        map.putIfAbsent(key, set);
    }

    public synchronized <T> void putIfAbsent(Class<T> key, Object implementation) {
        if (contains(key)) {
            Set<Object> objects = map.get(key);
            objects.add(implementation);
            return;
        }

        Set<Object> set = new HashSet<>();
        set.add(implementation);
        map.putIfAbsent(key, set);
    }

    public synchronized <T> void putIfAbsent(Class<T> key) {
        putIfAbsent(key, key);
    }

    public Set<Object> get(Class<?> clazz) {
        Set<Object> objects = map.get(clazz);

        if (scope == Scope.SINGLETON) {
            return objects;
        }

        objects = objects.stream().map(o -> createObject(o.getClass())).collect(Collectors.toSet());
        objects = map.put(clazz, objects);
        return objects;
    }

    private Object getSingleBean(Class<?> clazz) {
        Set<Object> objects = map.get(clazz);

        if (objects.size() != 1) {
            throw new IllegalStateException("Class '" + clazz.getName() + "' have more than 1 implementation registered");
        }

        Object objectAlreadySaved = objects.stream().findFirst().orElseThrow();

        if (scope == Scope.SINGLETON) {
            return objectAlreadySaved;
        }

        Object newObject = createObject(clazz);

        assert newObject != null;
        map.put(clazz, new HashSet<>(List.of(newObject)));
        return newObject;
    }

    @SuppressWarnings("unchecked")
    private <T> T createObject(Class<?> implementation) {
        try {
            Constructor<?>[] constructors = implementation.getConstructors();

            if (constructors.length != 1) {
                throw new IllegalStateException("Was expected only 1 constructor for class '" + implementation.getName() + "' but " + constructors.length + " was found");
            }

            Constructor<?> constructor = constructors[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] objects = Arrays.stream(parameterTypes).map(this::getSingleBean).toArray();
            return (T)constructor.newInstance(objects);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
