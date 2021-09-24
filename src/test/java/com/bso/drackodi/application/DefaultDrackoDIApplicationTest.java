package com.bso.drackodi.application;

import com.bso.drackodi.container.Container;
import com.bso.drackodi.container.DefaultContainerImpl;
import com.bso.drackodi.provider.BeanProvider;
import com.bso.drackodi.provider.DefaultBeanProviderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDrackoDIApplicationTest {

    private DefaultDrackoDIApplication subject;

    public static class Dummy { }

    @BeforeEach
    public void setup() {
        subject = new DefaultDrackoDIApplication();
    }

    @Test
    void testInit() {
        Container container = subject.init();
        assertThat(container).isNotNull().isExactlyInstanceOf(DefaultContainerImpl.class);
    }

    @Test
    void testBuild() {
        Container container = subject.init();
        container.register(Dummy.class);

        BeanProvider beanProvider = subject.buildProvider();
        assertThat(beanProvider).isNotNull().isExactlyInstanceOf(DefaultBeanProviderImpl.class);
        assertThat(beanProvider.getBean(Dummy.class)).isNotNull();
    }
}