package com.bso.drackodi.container;

import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.scope.Scope;

public interface Container {

    void register(Class<?> object);
    void register(Class<?> object, Scope scope);
    BeanProvider build();

}
