package com.bso.drackodi.provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.object.ObjectFactory;
import com.bso.drackodi.scope.Scope;

public class DefaultBeanProviderImpl implements BeanProvider {
	
	private final Map<Class<?>, ClassInfo> classInfoMap;
	private final ObjectFactory objectFactory;
	
	public DefaultBeanProviderImpl(Map<Class<?>, ClassInfo> classInfoMap, ObjectFactory objectFactory) {
		this.classInfoMap = classInfoMap;
		this.objectFactory = objectFactory;
		registerProviderItself();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> clazz) {
		List<ClassInfo> classInfosForReturn = classInfoMap
				.values()
				.stream()
				.filter(ci -> ci.isClassOrInterfaceOf(clazz))
				.filter(ClassInfo::hasImplementationCreated)
				.collect(Collectors.toList());
		
		synchronized (this) {
			classInfosForReturn.forEach(ci -> {
				if (ci.getScope() == Scope.TRANSIENT) {
					ci.setImplementation(objectFactory.createObject(ci.getClazz(), classInfoMap));
				}
			});
		}
		
		return classInfosForReturn
				.stream()
				.map(ClassInfo::getImplementation)
				.map(o -> (T)o)
				.findAny()
				.orElseThrow();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> getBeans(Class<T> clazz) {
		List<ClassInfo> classInfosForReturn = classInfoMap
				.values()
				.stream()
				.filter(ci -> ci.isClassOrInterfaceOf(clazz))
				.filter(ClassInfo::hasImplementationCreated)
				.collect(Collectors.toList());
		
		synchronized (this) {
			classInfosForReturn.forEach(ci -> {
				if (ci.getScope() == Scope.TRANSIENT) {
					ci.setImplementation(objectFactory.createObject(ci.getClazz(), classInfoMap));
				}
			});
		}
		
		return classInfosForReturn
				.stream()
				.map(ClassInfo::getImplementation)
				.map(o -> (T)o).collect(Collectors.toList());
	}

	private void registerProviderItself() {
		
		var classInfo = new ClassInfo(this.getClass(), Scope.SINGLETON, this.getClass().getInterfaces());
		classInfo.setImplementation(this);
		
		classInfoMap.putIfAbsent(getClass(), classInfo);
	}
}
