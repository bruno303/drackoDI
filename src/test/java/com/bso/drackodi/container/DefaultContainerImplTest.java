package com.bso.drackodi.container;

import com.bso.drackodi.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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

        DummyInterface dummyInterface = container.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);
    }

    @Test
    void testRegisterSameClassMultipleTimes() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation.class, Scope.TRANSIENT);

        Set<DummyInterface> dummys = container.getBeans(DummyInterface.class);
        assertThat(dummys)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void testGetSingleImplementationByInterfaceOfMultipleCandidates() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);

        DummyInterface dummyInterface = container.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isInstanceOfAny(DummyImplementation.class, DummyImplementation2.class);
    }

    @Test
    void testGetImplementationByImplementationItself() {
        container.register(DummyImplementation.class, Scope.TRANSIENT);
        container.register(DummyImplementation2.class, Scope.TRANSIENT);

        DummyInterface dummyInterface = container.getBean(DummyImplementation.class);
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

        Set<DummyInterface> dummyInterfaces = container.getBeans(DummyInterface.class);
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

        DummyInterface dummy1 = container.getBean(DummyInterface.class);
        DummyInterface dummy2 = container.getBean(DummyInterface.class);
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

        Set<DummyInterface> implementations = container.getBeans(DummyInterface.class);
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

        DummyInterface implementation = container.getBean(DummyInterface.class);
        DummyInterface implementation2 = container.getBean(DummyInterface.class);
        assertThat(implementation).isNotSameAs(implementation2);
    }
}
