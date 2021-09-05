package com.bso.drackodi.object;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.bso.drackodi.model.ClassInfo;

public class ObjectFactory {

	@SuppressWarnings("unchecked")
    public <T> T createObject(Class<?> implementation, Map<Class<?>, ClassInfo> classInfoMap) {
        try {
            Constructor<?>[] constructors = implementation.getConstructors();

            if (constructors.length != 1) {
                throw new IllegalStateException("Was expected only 1 constructor for class '" + implementation.getName() + "' but " + constructors.length + " was found");
            }

            Constructor<?> constructor = constructors[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            
            Object[] objects = Arrays.stream(parameterTypes).map(p -> this.getBeanOrCreate(p, classInfoMap)).toArray();
            
            return (T)constructor.newInstance(objects);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	@SuppressWarnings("unchecked")
	public <T> T getBeanOrCreate(Class<?> clazz, Map<Class<?>, ClassInfo> classInfoMap) {
		Set<ClassInfo> registeredClasses = classInfoMap
				.entrySet()
				.stream()
				.map(Entry::getValue)
				.filter(ci -> ci.isClassOrInterfaceOf(clazz))
				.collect(Collectors.toSet());
		
		if (registeredClasses.size() != 1) {
			throw new IllegalStateException("Expected only 1 bean of type '" + clazz.getName() + "'");
		}
		
		// single bean already registered
		ClassInfo classInfo = registeredClasses.stream().findAny().orElseThrow();
		if (classInfo.hasImplementationCreated()) {
			return (T)classInfo.getImplementation();
		}
		
		// handle creation
		Object implementationCreated = createObject(clazz, classInfoMap);
		classInfo.setImplementation(implementationCreated);
		return (T)implementationCreated;
	}
}
