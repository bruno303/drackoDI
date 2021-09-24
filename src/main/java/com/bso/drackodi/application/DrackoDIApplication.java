package com.bso.drackodi.application;

import com.bso.drackodi.container.Container;
import com.bso.drackodi.provider.BeanProvider;

public interface DrackoDIApplication {

    BeanProvider buildProvider();
    Container init();

}
