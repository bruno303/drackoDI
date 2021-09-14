package com.bso.drackodi.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BeanDefinitionInfo {

    private final String name;
    private final boolean primary;

    public boolean hasName() {
        return this.name != null;
    }
}
