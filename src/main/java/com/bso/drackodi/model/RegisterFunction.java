package com.bso.drackodi.model;

import com.bso.drackodi.provider.BeanProvider;

@FunctionalInterface
public interface RegisterFunction {

    Object createObject(BeanProvider beanProvider);

}
