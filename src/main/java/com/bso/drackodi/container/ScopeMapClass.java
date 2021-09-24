package com.bso.drackodi.container;

import com.bso.drackodi.model.BeanDefinitionInfo;
import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.model.RegisterFunction;
import com.bso.drackodi.model.exceptions.BeanWithSameTypeAndNameAlreadyExists;
import com.bso.drackodi.model.exceptions.PrimaryBeanWithSameTypeAlreadyRegistered;
import com.bso.drackodi.scope.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScopeMapClass {

    private final List<ClassInfo> classList = new ArrayList<>();

    public synchronized <T> void add(Class<T> clazz, Scope scope, RegisterFunction registerFunction) {
        this.add(clazz, scope, registerFunction, "", false);
    }

    public synchronized <T> void add(Class<T> clazz, Scope scope, RegisterFunction registerFunction,
                                     String beanName, boolean primary) {
        validate(clazz, beanName, primary);
        var beanDefinition = new BeanDefinitionInfo(beanName, primary);
        ClassInfo classInfo = new ClassInfo(clazz, scope, clazz.getInterfaces(), registerFunction, beanDefinition);
        this.classList.add(classInfo);
    }

	public synchronized List<ClassInfo> getClassList() {
		return classList;
	}

	public synchronized ClassInfo get(Class<?> clazz) {
        return classList.stream().filter(ci -> ci.getClazz().equals(clazz)).findAny().orElseThrow();
    }

    private <T> void validate(Class<T> clazz, String beanName, boolean primary) {
        List<ClassInfo> classInfosWithSameType = classList.stream()
                .filter(ci -> ci.getClazz().equals(clazz))
                .collect(Collectors.toList());

        List<ClassInfo> classInfosWithSameBeanName = classInfosWithSameType.stream()
                .filter(ci -> ci.hasName() && ci.getBeanName().equals(beanName))
                .collect(Collectors.toList());

        BeanWithSameTypeAndNameAlreadyExists.throwIf(!classInfosWithSameBeanName.isEmpty(), clazz, beanName);

        List<ClassInfo> classInfosThatArePrimaryToo = classInfosWithSameType
                .stream()
                .filter(ci -> ci.isPrimary() && primary)
                .collect(Collectors.toList());

        PrimaryBeanWithSameTypeAlreadyRegistered.throwIf(!classInfosThatArePrimaryToo.isEmpty(), clazz);
    }
}
