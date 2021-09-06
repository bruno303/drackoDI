package com.bso.drackodi.provider;

import java.util.List;

public interface BeanProvider {
	
	<T> T getBean(Class<T> clazz);
	<T> List<T> getBeans(Class<T> clazz);
	
}
