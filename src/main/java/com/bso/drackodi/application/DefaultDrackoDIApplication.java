package com.bso.drackodi.application;

import com.bso.drackodi.container.Container;
import com.bso.drackodi.container.DefaultContainerImpl;
import com.bso.drackodi.provider.BeanProvider;

public class DefaultDrackoDIApplication implements DrackoDIApplication {

    private Container container;

    @Override
    public final BeanProvider buildProvider() {
        return container.build();
    }

    @Override
    public final Container init() {
        if (container == null) {
            container = new DefaultContainerImpl();
        }
        return container;
    }
}
