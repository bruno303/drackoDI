package com.bso.drackodi.provider;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.object.ObjectFactory;
import com.bso.drackodi.scope.Scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultBeanProviderImplTest {

    private BeanProvider beanProvider;
    private ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
    private Map<Class<?>, ClassInfo> classInfoMap = new HashMap<>();

    public static class Dummy { }

    @BeforeEach
    public void setup() {
        beanProvider = new DefaultBeanProviderImpl(classInfoMap, objectFactory);
        classInfoMap.put(Dummy.class, new ClassInfo(Dummy.class, Scope.DEFAULT, Dummy.class.getInterfaces()));
    }
    
    @Test
    void testGetBeanNotExistent() {
        assertThrows(NoSuchElementException.class, () -> beanProvider.getBean(Dummy.class));
    }

    @Test
    void testGetBeanExistentWithoutImplementationCreated() {
        assertThrows(NoSuchElementException.class, () -> beanProvider.getBean(Dummy.class));
    }

    @Test
    void testGetBeanExistentWithImplementationCreated() {
        ClassInfo classInfo = classInfoMap.get(Dummy.class);
        classInfo.setImplementation(new Dummy());

        Dummy bean = beanProvider.getBean(Dummy.class);
        assertThat(bean).isNotNull();
    }
}
