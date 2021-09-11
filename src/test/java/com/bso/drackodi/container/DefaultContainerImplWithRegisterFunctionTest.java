package com.bso.drackodi.container;

import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultContainerImplWithRegisterFunctionTest {

    public interface DummyInterface { }
    public static class DummyImplementation implements DummyInterface {
        public String value = "xpto";
    }

    public static class DummyImplementation2 {
        private final DummyImplementation dependency;

        public DummyImplementation2(DummyImplementation dependency) {
            this.dependency = dependency;
        }

        public DummyImplementation getDependency() {
            return dependency;
        }
    }

    public static class DummyImplementation3 {
        private final DummyInterface dependencyInterface;
        private final DummyImplementation2 dependencyImpl2;

        public DummyImplementation3(DummyInterface dependencyInterface, DummyImplementation2 dependencyImpl2) {
            this.dependencyInterface = dependencyInterface;
            this.dependencyImpl2 = dependencyImpl2;
        }

        public DummyInterface getDependencyInterface() {
            return dependencyInterface;
        }

        public DummyImplementation2 getDependencyImpl2() {
            return dependencyImpl2;
        }
    }

    private DefaultContainerImpl container;

    @BeforeEach
    public void setup() {
        container = new DefaultContainerImpl();
    }

    @Test
    void testRegisterBeanWithCustomMethodWithoutUseBeanProvider() {
        var dependency = new DummyImplementation();

        container.register(DummyImplementation2.class,
                beanProvider -> new DummyImplementation2(dependency));

        BeanProvider beanProvider = container.build();

        DummyImplementation2 impl2 = beanProvider.getBean(DummyImplementation2.class);
        assertThat(impl2)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        assertThat(impl2.getDependency()).isEqualTo(dependency);
    }

    @Test
    void testRegisterBeanWithCustomMethodUsingBeanProvider() {
        container.register(DummyImplementation2.class, beanProvider -> {

            DummyInterface impl = beanProvider.getBean(DummyInterface.class);
            ((DummyImplementation) impl).value = "UPDATED VALUE";

            return new DummyImplementation2((DummyImplementation) impl);
        });

        container.register(DummyImplementation.class);
        BeanProvider beanProvider = container.build();

        DummyImplementation2 impl2 = beanProvider.getBean(DummyImplementation2.class);
        assertThat(impl2)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        assertThat(impl2.getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        assertThat(impl2.getDependency().value)
                .isEqualTo("UPDATED VALUE");
    }

    @Test
    void testRegisterBeanWithCustomMethodUsingBeanProviderAndScope() {
        container.register(DummyImplementation2.class, Scope.TRANSIENT, beanProvider -> {

            DummyImplementation impl = beanProvider.getBean(DummyImplementation.class);
            impl.value = "UPDATED VALUE";

            return new DummyImplementation2(impl);
        });

        container.register(DummyImplementation.class);
        BeanProvider beanProvider = container.build();

        DummyImplementation2 impl2 = beanProvider.getBean(DummyImplementation2.class);
        assertThat(impl2)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        assertThat(impl2.getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        assertThat(impl2.getDependency().value).isEqualTo("UPDATED VALUE");

        DummyImplementation2 newImpl2 = beanProvider.getBean(DummyImplementation2.class);
        assertThat(newImpl2)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class)
                .isNotSameAs(impl2);

        assertThat(newImpl2.getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class)
                .isSameAs(impl2.getDependency());
    }

    @Test
    void testRegisterBeanWithCustomMethodRecursively() {
        container.register(DummyImplementation2.class, beanProvider -> {

            DummyImplementation impl = beanProvider.getBean(DummyImplementation.class);
            impl.value = "UPDATED VALUE";

            return new DummyImplementation2(impl);
        });

        container.register(DummyImplementation.class);
        container.register(DummyImplementation3.class, beanProvider -> {
            DummyInterface dependency = beanProvider.getBean(DummyInterface.class);
            DummyImplementation2 impl2 = beanProvider.getBean(DummyImplementation2.class);

            return new DummyImplementation3(dependency, impl2);
        });

        BeanProvider beanProvider = container.build();

        DummyImplementation2 impl2 = beanProvider.getBean(DummyImplementation2.class);
        assertThat(impl2)
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation2.class);

        assertThat(impl2.getDependency())
                .isNotNull()
                .isExactlyInstanceOf(DummyImplementation.class);

        assertThat(impl2.getDependency().value).isEqualTo("UPDATED VALUE");

        DummyImplementation3 impl3 = beanProvider.getBean(DummyImplementation3.class);
        assertThat(impl3).isNotNull().isExactlyInstanceOf(DummyImplementation3.class);
        assertThat(impl3.getDependencyImpl2()).isNotNull().isSameAs(impl2);
        assertThat(impl3.getDependencyInterface()).isNotNull().isExactlyInstanceOf(DummyImplementation.class);
    }
}
