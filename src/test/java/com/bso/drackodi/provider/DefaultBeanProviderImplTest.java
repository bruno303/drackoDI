package com.bso.drackodi.provider;

import com.bso.drackodi.container.ScopeMapClass;
import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.model.exceptions.BeanNotFoundException;
import com.bso.drackodi.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultBeanProviderImplTest {

    private BeanProvider beanProvider;
    private final ScopeMapClass classManager = new ScopeMapClass();

    public static class DummyWithNullImplementation { }
    public static class DummyNotRegistered { }
    public static class Dummy {
        private final boolean primary;

        public Dummy(boolean primary) {
            this.primary = primary;
        }

        public boolean isPrimary() {
            return primary;
        }
    }

    @BeforeEach
    public void setup() {
        classManager.add(DummyWithNullImplementation.class, Scope.DEFAULT, bp -> null);
    }
    
    @Test
    void testGetBeanNotRegistered() {
        beanProvider = new DefaultBeanProviderImpl(classManager);
        assertThrows(BeanNotFoundException.class, () -> beanProvider.getBean(DummyNotRegistered.class));
    }

    @Test
    void testGetBeanExistentWithoutImplementationCreated() {
        beanProvider = new DefaultBeanProviderImpl(classManager);
        assertThrows(BeanNotFoundException.class, () -> beanProvider.getBean(DummyWithNullImplementation.class));
    }

    @Test
    void testGetBeanExistentWithImplementationCreated() {
        beanProvider = new DefaultBeanProviderImpl(classManager);
        ClassInfo classInfo = classManager.get(DummyWithNullImplementation.class);
        classInfo.setImplementation(new DummyWithNullImplementation());

        DummyWithNullImplementation bean = beanProvider.getBean(DummyWithNullImplementation.class);
        assertThat(bean).isNotNull();
    }

    @Test
    void testGetBeanReturnsPrimaryBean() {
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(true), "xpto", true);
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(false), "xpto2", false);
        beanProvider = new DefaultBeanProviderImpl(classManager);

        Dummy bean = beanProvider.getBean(Dummy.class);
        Dummy bean2 = beanProvider.getBean(Dummy.class);
        Dummy bean3 = beanProvider.getBean(Dummy.class);

        assertThat(bean).isNotNull();
        assertThat(bean2).isNotNull();
        assertThat(bean3).isNotNull();

        assertThat(bean.isPrimary()).isTrue();
        assertThat(bean2.isPrimary()).isTrue();
        assertThat(bean3.isPrimary()).isTrue();

        assertThat(bean).isSameAs(bean2).isSameAs(bean3);
    }

    @Test
    void testGetBeansReturnsPrimaryAndNonPrimaryBeans() {
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(true), "xpto", true);
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(false), "xpto2", false);
        beanProvider = new DefaultBeanProviderImpl(classManager);

        List<Dummy> beans = beanProvider.getBeans(Dummy.class);

        assertThat(beans).isNotNull().hasSize(2);

        assertThat(beans.get(0).isPrimary()).isNotEqualTo(beans.get(1).isPrimary());

        assertThat(beans.get(0)).isNotSameAs(beans.get(1));
    }

    @Test
    void testGetBeansByName() {
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(true), "xpto", true);
        classManager.add(Dummy.class, Scope.DEFAULT, bp -> new Dummy(false), "xpto2", false);
        beanProvider = new DefaultBeanProviderImpl(classManager);

        Dummy bean1 = beanProvider.getBean(Dummy.class, "xpto");
        assertThat(bean1).isNotNull();
        assertThat(bean1.isPrimary()).isTrue();

        Dummy bean2 = beanProvider.getBean(Dummy.class, "xpto2");
        assertThat(bean2).isNotNull();
        assertThat(bean2.isPrimary()).isFalse();
    }
}
