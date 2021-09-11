package com.bso.drackodi.model.exceptions;

public class BeanNotFoundException extends DrackoDIException {

    public BeanNotFoundException(Class<?> clazz) {
        super(String.format("Bean with type '%s' not found. You must register it in the container.",
                clazz.getName()));
    }

    public static void throwIf(boolean condition, Class<?> clazz) {
        if (condition) {
            throw new BeanNotFoundException(clazz);
        }
    }
}
