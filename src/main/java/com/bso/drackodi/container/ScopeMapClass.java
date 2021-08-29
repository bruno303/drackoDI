package com.bso.drackodi.container;

import com.bso.drackodi.scope.Scope;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
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

    public synchronized <T> void putIfAbsent(Class<T> key, Object value) {
        if (contains(key)) {
            Set<Object> objects = map.get(key);
            objects.add(value);
            return;
        }

        Set<Object> set = new HashSet<>();
        set.add(value);
        map.putIfAbsent(key, set);
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

    @SuppressWarnings("unchecked")
    private <T> T createObject(Class<?> implementation) {
        try {
            return (T)implementation.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
