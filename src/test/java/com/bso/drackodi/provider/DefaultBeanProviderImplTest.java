package com.bso.drackodi.provider;

import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.model.exceptions.BeanNotFoundException;
import com.bso.drackodi.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultBeanProviderImplTest {

    private BeanProvider beanProvider;
    private final Map<Class<?>, ClassInfo> classInfoMap = new HashMap<>();

    public static class DummyWithNullImplementation { }
    public static class DummyNotRegistered { }

    @BeforeEach
    public void setup() {
        classInfoMap.put(DummyWithNullImplementation.class,
                new ClassInfo(DummyWithNullImplementation.class, Scope.DEFAULT, DummyWithNullImplementation.class.getInterfaces(), bp -> null));
        beanProvider = new DefaultBeanProviderImpl(classInfoMap);
    }
    
    @Test
    void testGetBeanNotRegistered() {
        assertThrows(BeanNotFoundException.class, () -> beanProvider.getBean(DummyNotRegistered.class));
    }

    @Test
    void testGetBeanExistentWithoutImplementationCreated() {
        assertThrows(BeanNotFoundException.class, () -> beanProvider.getBean(DummyWithNullImplementation.class));
    }

    @Test
    void testGetBeanExistentWithImplementationCreated() {
        ClassInfo classInfo = classInfoMap.get(DummyWithNullImplementation.class);
        classInfo.setImplementation(new DummyWithNullImplementation());

        DummyWithNullImplementation bean = beanProvider.getBean(DummyWithNullImplementation.class);
        assertThat(bean).isNotNull();
    }
}
