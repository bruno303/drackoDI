package com.bso.drackodi.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.bso.drackodi.model.exceptions.SingletonObjectAlreadyCreatedException;
import com.bso.drackodi.scope.Scope;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClassInfo {

	private final Class<?> clazz;
	private final Scope scope;
	private final List<Class<?>> interfaces;
	private Object implementation;
	private final RegisterFunction registerFunction;
	private final BeanDefinitionInfo beanDefinition;
	
	public ClassInfo(Class<?> clazz, Scope scope, Class<?>[] interfaces, RegisterFunction registerFunction) {
		this(clazz, scope, Arrays.stream(interfaces).collect(Collectors.toList()),
				registerFunction,
				new BeanDefinitionInfo("", false));
	}

	public ClassInfo(Class<?> clazz, Scope scope, Class<?>[] interfaces, RegisterFunction registerFunction, BeanDefinitionInfo beanDefinition) {
		this(clazz, scope, Arrays.stream(interfaces).collect(Collectors.toList()),
				registerFunction,
				beanDefinition);
	}
	
	public void setImplementation(Object implementation) {
		if (this.implementation != null && this.scope == Scope.SINGLETON) {
			throw new SingletonObjectAlreadyCreatedException(clazz);
		}
		
		this.implementation = implementation;
	}
	
	public boolean hasImplementationCreated() {
		return this.implementation != null;
	}

	
	public boolean isClassOrInterfaceOf(Class<?> clazz) {
		return this.clazz.equals(clazz) || this.interfaces.stream().anyMatch(i -> i.equals(clazz));
	}

	public boolean hasCustomCreation() {
		return this.registerFunction != null;
	}

	public boolean needCreateImplementation() {
		return this.scope == Scope.TRANSIENT || !this.hasImplementationCreated();
	}

	public boolean hasName() {
		return this.beanDefinition.hasName();
	}

	public String getBeanName() {
		return this.beanDefinition.getName();
	}

	public boolean isPrimary() {
		return this.beanDefinition.isPrimary();
	}
}
