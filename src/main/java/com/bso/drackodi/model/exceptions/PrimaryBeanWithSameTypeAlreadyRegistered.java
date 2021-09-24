package com.bso.drackodi.model.exceptions;

public class PrimaryBeanWithSameTypeAlreadyRegistered extends DrackoDIException {
    public PrimaryBeanWithSameTypeAlreadyRegistered(Class<?> clazz) {
        super(String.format("A bean with type '%s'and marked as primary is already registered. Define only 1 primary bean for each type", clazz.getName()));
    }

    public static void throwIf(boolean condition, Class<?> clazz) {
        if (condition) throw new PrimaryBeanWithSameTypeAlreadyRegistered(clazz);
    }
}
