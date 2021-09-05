package com.bso.drackodi.model.exceptions;

public class ClassIsInterfaceException extends DrackoDIException {

	public ClassIsInterfaceException(Class<?> clazz) {
		super(String.format("Class '%s' is an interface and can't be registered. Try register a concrete class.", clazz.getName()));
	}
	
	public static void throwIf(boolean condition, Class<?> clazz) {
		if (condition) throw new ClassIsInterfaceException(clazz);
	}
}
