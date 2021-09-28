package com.bso.drackodi.provider;

import java.util.Collection;

public interface BeanProvider {

    <T> T getBean(Class<T> clazz);
    <T> T getBean(Class<T> clazz, String name);
	<T> Collection<T> getBeans(Class<T> clazz);

}
