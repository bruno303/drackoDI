package com.bso.drackodi.container;

import com.bso.drackodi.model.RegisterFunction;
import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.scope.Scope;

public interface Container {

    void register(Class<?> object);
    void register(Class<?> object, Scope scope);
    void register(Class<?> object, String beanName, boolean primary);
    void register(Class<?> object, RegisterFunction registerFunction);
    void register(Class<?> object, Scope scope, RegisterFunction registerFunction);
    void register(Class<?> object, Scope scope, String beanName, boolean primary);
    void register(Class<?> object, Scope scope, RegisterFunction registerFunction,
                  String beanName, boolean primary);
    BeanProvider build();

}
