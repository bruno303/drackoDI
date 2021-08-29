package com.bso.drackodi.container;

import com.bso.drackodi.scope.Scope;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultContainerImpl implements Container {

    private final Map<Scope, ScopeMapClass> scopeMapClasses;
    private final ScopeMapClass transients = new ScopeMapClass(Scope.TRANSIENT);
    private final ScopeMapClass singletons = new ScopeMapClass(Scope.SINGLETON);

    public DefaultContainerImpl() {
        this.scopeMapClasses = new EnumMap<>(Scope.class);
        this.scopeMapClasses.putIfAbsent(transients.getScope(), transients);
        this.scopeMapClasses.putIfAbsent(singletons.getScope(), singletons);
    }

    @Override
    public synchronized void register(Class<?> object) {
        register(object, Scope.DEFAULT);
    }

    @Override
    public synchronized void register(Class<?> object, Scope scope) {
        doRegister(object, scope);
    }

    @Override
    public synchronized <T> T getBean(Class<T> object) {
        Optional<Set<T>> resultsOpt = getImplementations(object);

        if (resultsOpt.isEmpty()) {
            throw new NoSuchElementException("Element with class '" + object.getName() + "' not found");
        }

        return resultsOpt.get().stream().findFirst().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<Set<T>> getImplementations(Class<T> object) {
        if (transients.contains(object)) {
            return Optional.of(transients.get(object).stream().map(o -> (T)o).collect(Collectors.toSet()));
        }

        if (singletons.contains(object)) {
            return Optional.of(singletons.get(object).stream().map(o -> (T)o).collect(Collectors.toSet()));
        }

        return Optional.empty();
    }

    @Override
    public <T> Set<T> getBeans(Class<T> object) {
        Optional<Set<T>> resultsOpt = getImplementations(object);

        if (resultsOpt.isEmpty()) {
            throw new NoSuchElementException("Element with class '" + object.getName() + "' not found");
        }

        return resultsOpt.get();
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

    private void doRegister(Class<?> clazz, Scope scope) {

        ScopeMapClass scopeMapClass = scopeMapClasses.get(scope);

        if (scopeMapClass.contains(clazz)) {
            return;
        }

        Object object = createObject(clazz);

        for (var iface : clazz.getInterfaces()) {
            scopeMapClass.putIfAbsent(iface, object);
        }

        scopeMapClass.putIfAbsent(clazz, object);
    }
}
