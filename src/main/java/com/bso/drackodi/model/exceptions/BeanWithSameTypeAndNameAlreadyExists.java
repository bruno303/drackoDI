package com.bso.drackodi.model.exceptions;

public class BeanWithSameTypeAndNameAlreadyExists extends DrackoDIException {
    public BeanWithSameTypeAndNameAlreadyExists(Class<?> clazz, String name) {
        super(String.format("A bean with type '%s'and name '%s' is already registered. Try changing it's name", clazz.getName(), name));
    }

    public static void throwIf(boolean condition, Class<?> clazz, String name) {
        if (condition) throw new BeanWithSameTypeAndNameAlreadyExists(clazz, name);
    }
}
