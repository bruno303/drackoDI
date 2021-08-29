package com.bso.drackodi.container;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContainerImplConstructorsWithMultipleParametersTest {

    public interface DummyInterface { }
    public static class DummyDependency { }
    public static class DummyDependency2 { }

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
        private final DummyDependency2 dependency2;

        public DummyImplementation(DummyDependency dependency, DummyDependency2 dependency2) {
            this.dependency = dependency;
            this.dependency2 = dependency2;
        }

        public DummyDependency getDependency() {
            return this.dependency;
        }
        public DummyDependency2 getDependency2() {
            return this.dependency2;
        }
    }

    public static class DummyImplementation2 implements DummyInterface {

        private final DummyDependency2 dependency2;
        private final DummyDependency3 dependency3;

        public DummyImplementation2(DummyDependency2 dependency2, DummyDependency3 dependency3) {
            this.dependency2 = dependency2;
            this.dependency3 = dependency3;
        }

        public DummyDependency2 getDependency2() {
            return this.dependency2;
        }
        public DummyDependency3 getDependency3() {
            return this.dependency3;
        }
    }

    private DefaultContainerImpl container;

    @BeforeEach
    public void setup() {
        container = new DefaultContainerImpl();
    }

    @Test
    void testCreateObjectWithConstructorWithTwoIndependentParameters() {
        container.register(DummyDependency.class);
        container.register(DummyDependency2.class);
        container.register(DummyImplementation.class);

        DummyInterface dummyInterface = container.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        DummyDependency dependency = ((DummyImplementation) dummyInterface).getDependency();

        assertThat(dependency)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency.class);

        DummyDependency2 dependency2 = ((DummyImplementation) dummyInterface).getDependency2();
        assertThat(dependency2)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency2.class);
    }

    @Test
    void testCreateObjectWithConstructorWithTwoDependentParameters() {
        container.register(DummyDependency2.class);
        container.register(DummyDependency3.class);
        container.register(DummyImplementation2.class);

        DummyInterface dummyInterface = container.getBean(DummyInterface.class);
        assertThat(dummyInterface)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        DummyImplementation2 implementation = (DummyImplementation2) dummyInterface;
        DummyDependency2 dependency2 = implementation.getDependency2();

        assertThat(dependency2)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency2.class);

        DummyDependency3 dependency3 = implementation.getDependency3();
        assertThat(dependency3)
                .isNotNull()
                .isExactlyInstanceOf(DummyDependency3.class);

        DummyDependency2 dependency2From3 = dependency3.getDependency();
        assertThat(dependency2From3)
                .isNotNull()
                .isSameAs(dependency2);
    }
}
