package com.bso.drackodi.container;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bso.drackodi.provider.BeanProvider;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContainerImplConstructorsWithOneParameterTest {

    public interface DummyInterface { }
    public static class DummyDependency { }

    public static class DummyDependency2 {
        private final DummyDependency dependency;

        public DummyDependency2(DummyDependency dependency) {
            this.dependency = dependency;
        }

        public DummyDependency getDependency() {
            return this.dependency;
        }
    }

    public static class DummyDependency3 {
        private final DummyDependency2 dependency;

        public DummyDependency3(DummyDependency2 dependency) {
            this.dependency = dependency;
        }

        public DummyDependency2 getDependency() {
            return this.dependency;
        }
    }

    public static class DummyImplementation implements DummyInterface {

        private final DummyDependency dependency;

        public DummyImplementation(DummyDependency dependency) {
            this.dependency = dependency;
        }

        public DummyDependency getDependency() {
            return this.dependency;
        }
    }

    public static class DummyImplementation2 implements DummyInterface {

        private final DummyDependency3 dependency;

        public DummyImplementation2(DummyDependency3 dependency) {
            this.dependency = dependency;
        }

        public DummyDependency3 getDependency() {
            return this.dependency;
        }
    }

    private DefaultContainerImpl container;

    @BeforeEach
    public void setup() {
        container = new DefaultContainerImpl();
    }

    @Test
    void testCreateObjectWithConstructorWithOneParameter() {
        container.register(DummyDependency.class);
        container.register(DummyImplementation.class);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        assertThat(((DummyImplementation)dummyInterface).getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency.class);
    }
    
    @Test
    void testCreateObjectWithConstructorWithOneParameterRegisteredInDifferentOrder() {
    	container.register(DummyImplementation.class);
        container.register(DummyDependency.class);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        assertThat(((DummyImplementation)dummyInterface).getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency.class);
    }

    @Test
    void testCreateObjectWithConstructorWithOneParameterThatHasDependenciesToo() {
        container.register(DummyDependency.class);
        container.register(DummyDependency2.class);
        container.register(DummyDependency3.class);
        container.register(DummyImplementation2.class);
        BeanProvider beanProvider = container.build();

        DummyInterface dummyInterface = beanProvider.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        DummyDependency3 dependency3 = ((DummyImplementation2) dummyInterface).getDependency();

        assertThat(dependency3)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency3.class);

        DummyDependency2 dependency2 = dependency3.getDependency();

        assertThat(dependency2)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency2.class);

        DummyDependency dependency = dependency2.getDependency();

        assertThat(dependency)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency.class);
    }
}
