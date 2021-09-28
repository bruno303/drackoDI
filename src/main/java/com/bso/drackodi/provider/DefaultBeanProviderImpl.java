package com.bso.drackodi.provider;

import com.bso.drackodi.container.ScopeMapClass;
import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.model.OrderedObject;
import com.bso.drackodi.model.exceptions.BeanNotFoundException;
import com.bso.drackodi.scope.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultBeanProviderImpl implements BeanProvider {
	
	private final ScopeMapClass classManager;

	public DefaultBeanProviderImpl(ScopeMapClass classManager) {
		this.classManager = classManager;
		register();
	}

	private void register() {
		for (ClassInfo classInfo : classManager.getClassList()) {

			if (!classInfo.hasImplementationCreated()) {
				classInfo.setImplementation(createObject(classInfo));
			}
		}

		registerProviderItself();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> clazz, String name) {
		List<OrderedObject> orderedBeans = doGetBeans(clazz, name);

		return orderedBeans
				.stream()
				.map(OrderedObject::getImplementation)
				.map(o -> (T)o)
				.findAny()
				.orElseThrow();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> clazz) {
		List<OrderedObject> orderedBeans = doGetBeans(clazz);

		return orderedBeans
				.stream()
				.map(OrderedObject::getImplementation)
				.map(o -> (T)o)
				.findAny()
				.orElseThrow();
	}

	@Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getBeans(Class<T> clazz) {
		List<OrderedObject> orderedObjects = doGetBeans(clazz);

		return orderedObjects.stream()
				.map(OrderedObject::getImplementation)
				.map(o -> (T)o)
				.collect(Collectors.toList());
	}

	private List<OrderedObject> doGetBeans(Class<?> clazz) {
		return doGetBeans(clazz, null);
	}

	private List<OrderedObject> doGetBeans(Class<?> clazz, String name) {
		List<ClassInfo> classInfosForReturn = classManager
				.getClassList()
				.stream()
				.filter(ci -> ci.isClassOrInterfaceOf(clazz))
				.collect(Collectors.toList());

		if (name != null && name.length() > 0) {
			classInfosForReturn = classInfosForReturn.stream()
					.filter(c -> c.getBeanName().equals(name))
					.collect(Collectors.toList());
		}
		
		synchronized (this) {
			classInfosForReturn.forEach(ci -> {
				if (ci.needCreateImplementation()) {
					ci.setImplementation(createObject(ci));
				}
			});
		}

		var objectsToReturn = classInfosForReturn
				.stream()
				.filter(ClassInfo::hasImplementationCreated)
                .map(ci -> new OrderedObject(ci.getImplementation(), ci.isPrimary()))
				.collect(Collectors.toList());

		BeanNotFoundException.throwIf(objectsToReturn.isEmpty(), clazz);

		objectsToReturn.sort(new OrderedObject.OrderedObjectComparator());
		return objectsToReturn;
	}

	private void registerProviderItself() {
		
		var classInfo = new ClassInfo(this.getClass(), Scope.SINGLETON,
				this.getClass().getInterfaces(), null);
		classInfo.setImplementation(this);
		
		classManager.getClassList().add(classInfo);
	}

	private <T> T createObject(ClassInfo classInfo) {
		if (classInfo.hasCustomCreation()) {
			return createObjectUsingCustomFunction(classInfo);
		}

		return createObjectUsingConstructor(classInfo);
	}

	@SuppressWarnings("unchecked")
	private <T> T createObjectUsingCustomFunction(ClassInfo classInfo) {
		return (T)classInfo.getRegisterFunction().createObject(this);
	}

	@SuppressWarnings("unchecked")
	private <T> T createObjectUsingConstructor(ClassInfo classInfo) {
		try {
			Class<?> implementation = classInfo.getClazz();
			Constructor<?>[] constructors = implementation.getConstructors();

			if (constructors.length != 1) {
				throw new IllegalStateException("Was expected only 1 constructor for class '" + implementation.getName() + "' but " + constructors.length + " was found");
			}

			Constructor<?> constructor = constructors[0];
			Class<?>[] parameterTypes = constructor.getParameterTypes();

			Object[] objects = Arrays
					.stream(parameterTypes)
					.map(this::getBean)
					.toArray();

			return (T)constructor.newInstance(objects);
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
