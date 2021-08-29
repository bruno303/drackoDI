package com.bso.drackodi.container;

import com.bso.drackodi.scope.Scope;

import java.util.Set;

public interface Container {

    void register(Class<?> object);
    void register(Class<?> object, Scope scope);
    <T> T getBean(Class<T> object);
    <T> Set<T> getBeans(Class<T> object);

}
