package com.bso.drackodi.container;

import com.bso.drackodi.model.exceptions.ContainerAlreadyBuildedException;
import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.provider.DefaultBeanProviderImpl;
import com.bso.drackodi.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultContainerImplTest {

    public interface DummyInterface { }
    public static class DummyImplementation implements DummyInterface { }
    public static class DummyImplementation2 implements DummyInterface { }

    private DefaultContainerImpl container;

    @BeforeEach
    public void setup() {
        container = new DefaultContainerImpl();
    }

    @Test
    void testGetImplementationByInterface() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);
    }

    @Test
    void testRegisterSameClassMultipleTimes() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        List<DummyInterface> dummys = beanProvider.getBeans(DummyInterface.class);
        assertThat(dummys)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void testGetSingleImplementationByInterfaceOfMultipleCandidates() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
    }

    @Test
    void testGetImplementationByImplementationItself() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyImplementation.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);
    }

    @Test
    void testGetImplementationsByInterface() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        List<DummyInterface> dummyInterfaces = beanProvider.getBeans(DummyInterface.class);
        assertThat(dummyInterfaces)
                .isNotNull()
                .hasSize(2);

        ArrayList<DummyInterface> implementationsAsList = new ArrayList<>(dummyInterfaces);
        assertThat(implementationsAsList.get(0)).isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
        assertThat(implementationsAsList.get(1)).isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
        assertThat(implementationsAsList.get(0)).isNotExactlyInstanceOf(implementationsAsList.get(1).getClass());
    }

    @Test
    void testGetImplementationOfSingletonByInterfaceMultipleTimes() {
        container.register(DummyImplementation.class);
        container.register(DummyImplementation.class);
        container.register(DummyImplementation.class);
        BeanProvider beanProvider = container.build();

        DummyInterface dummy1 = beanProvider.getBean(DummyInterface.class);
        DummyInterface dummy2 = beanProvider.getBean(DummyInterface.class);
        assertThat(dummy1)
                .isNotNull()
                .isEqualTo(dummy2);
    }

    @Test
    void testGetImplementationsOfSingletonByInterfaceMultipleTimes() {
        container.register(DummyImplementation.class);
        container.register(DummyImplementation2.class);
        container.register(DummyImplementation.class);
        container.register(DummyImplementation2.class);
        BeanProvider beanProvider = container.build();

        List<DummyInterface> implementations = beanProvider.getBeans(DummyInterface.class);
        assertThat(implementations)
                .isNotNull()
                .hasSize(2);

        ArrayList<DummyInterface> implementationsAsList = new ArrayList<>(implementations);
        assertThat(implementationsAsList.get(0)).isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
        assertThat(implementationsAsList.get(1)).isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
        assertThat(implementationsAsList.get(0)).isNotExactlyInstanceOf(implementationsAsList.get(1).getClass());
    }

    @Test
    void testTransientBeanReturnDifferentObjectOnEachCall() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        BeanProvider beanProvider = container.build();

        DummyInterface implementation = beanProvider.getBean(DummyInterface.class);
        DummyInterface implementation2 = beanProvider.getBean(DummyInterface.class);
        assertThat(implementation).isNotSameAs(implementation2);
    }

    @Test
    void testContainerRegisteringItselfAsSingletonWhenCreated() {
    	BeanProvider beanProvider = container.build();
    	
    	BeanProvider beanProvider2 = beanProvider.getBean(BeanProvider.class);
    	DefaultBeanProviderImpl beanProvider3 = beanProvider.getBean(DefaultBeanProviderImpl.class);
        assertThat(beanProvider).isSameAs(beanProvider2);
        assertThat(beanProvider.getBean(BeanProvider.class)).isSameAs(beanProvider3);
    }
    
    @Test
    void testRegisterAfterBuildProvider() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.build();
        
        assertThrows(ContainerAlreadyBuildedException.class,
        		() -> container.register(DummyImplementation.class, Scope.TRANSIENT));
    }
    
    @Test
    void testBuildAgainAfterBuildProvider() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.build();
        
        assertThrows(ContainerAlreadyBuildedException.class,
        		() -> container.build());
    }
}
