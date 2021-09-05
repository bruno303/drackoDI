package com.bso.drackodi.container;

import java.util.concurrent.atomic.AtomicBoolean;

import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.model.exceptions.ClassIsInterfaceException;
import com.bso.drackodi.model.exceptions.ContainerAlreadyBuildedException;
import com.bso.drackodi.object.ObjectFactory;
import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.provider.DefaultBeanProviderImpl;
import com.bso.drackodi.scope.Scope;

public class DefaultContainerImpl implements Container {

    private final ScopeMapClass scopeMapClass = new ScopeMapClass();
    private final ObjectFactory objectFactory = new ObjectFactory();
    private AtomicBoolean builded = new AtomicBoolean(false);

    @Override
    public synchronized void register(Class<?> object) {
        register(object, Scope.DEFAULT);
    }

    @Override
    public synchronized void register(Class<?> object, Scope scope) {
        doRegister(object, scope);
    }

    private void doRegister(Class<?> clazz, Scope scope) {
    	
    	validateIfNotBuildedYet();
    	ClassIsInterfaceException.throwIf(clazz.isInterface(), clazz);
        
        scopeMapClass.putIfAbsent(clazz, scope);
    }

	@Override
	public synchronized BeanProvider build() {
		validateIfNotBuildedYet();
		
		for (ClassInfo classInfo : scopeMapClass.getMap().values()) {
			
			if (!classInfo.hasImplementationCreated()) {
				Object implementation = objectFactory.createObject(classInfo.getClazz(), scopeMapClass.getMap());
				
				classInfo.setImplementation(implementation);
			}
		}
		
		builded.set(true);
		
		return new DefaultBeanProviderImpl(scopeMapClass.getMap(), objectFactory);
	}
	
	private void validateIfNotBuildedYet() {
		ContainerAlreadyBuildedException.throwIf(builded.get());
	}
}
