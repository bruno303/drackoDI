package com.bso.drackodi.container;

import com.bso.drackodi.model.RegisterFunction;
import com.bso.drackodi.model.exceptions.ClassIsInterfaceException;
import com.bso.drackodi.model.exceptions.ContainerAlreadyBuildedException;
import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.provider.DefaultBeanProviderImpl;
import com.bso.drackodi.scope.Scope;

import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultContainerImpl implements Container {

    private final ScopeMapClass scopeMapClass = new ScopeMapClass();
    private final AtomicBoolean builded = new AtomicBoolean(false);

    @Override
    public synchronized void register(Class<?> object) {
        register(object, Scope.DEFAULT);
    }

    @Override
    public synchronized void register(Class<?> object, String beanName, boolean primary) {
        register(object, Scope.DEFAULT, beanName, primary);
    }

    @Override
    public synchronized void register(Class<?> object, Scope scope) {
        doRegister(object, scope, null);
    }

    @Override
    public synchronized void register(Class<?> object, Scope scope, String beanName, boolean primary) {
        doRegister(object, scope, null, beanName, primary);
    }

    @Override
    public synchronized void register(Class<?> object, Scope scope, RegisterFunction registerFunction,
                                      String beanName, boolean primary) {
        doRegister(object, scope, registerFunction, beanName, primary);
    }

    private void doRegister(Class<?> clazz, Scope scope, RegisterFunction registerFunction) {
        validateBeforeRegister(clazz);
        scopeMapClass.add(clazz, scope, registerFunction);
    }

    private void doRegister(Class<?> clazz, Scope scope, RegisterFunction registerFunction,
                            String beanName, boolean primary) {
        validateBeforeRegister(clazz);
        scopeMapClass.add(clazz, scope, registerFunction, beanName, primary);
    }

    private void validateBeforeRegister(Class<?> clazz) {
        validateIfNotBuildedYet();
        ClassIsInterfaceException.throwIf(clazz.isInterface(), clazz);
    }

	@Override
	public synchronized BeanProvider build() {
		validateIfNotBuildedYet();
		builded.set(true);
        return new DefaultBeanProviderImpl(scopeMapClass);
    }

	private void validateIfNotBuildedYet() {
		ContainerAlreadyBuildedException.throwIf(builded.get());
	}

    @Override
    public void register(Class<?> object, RegisterFunction registerFunction) {
        doRegister(object, Scope.DEFAULT, registerFunction);
    }

    @Override
    public void register(Class<?> object, Scope scope, RegisterFunction registerFunction) {
        doRegister(object, scope, registerFunction);
    }
}
