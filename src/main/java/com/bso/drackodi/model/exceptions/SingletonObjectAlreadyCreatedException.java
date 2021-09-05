package com.bso.drackodi.model.exceptions;

public class SingletonObjectAlreadyCreatedException extends DrackoDIException {

	public SingletonObjectAlreadyCreatedException(Class<?> clazz) {
		super(String.format("The object of type '%s' already exists and is SINGLETON. Creation of another instance is not allowed",
				clazz.getName()));
	}
	
	public static void throwIf(boolean condition, Class<?> clazz) {
		if (condition) {
			throw new SingletonObjectAlreadyCreatedException(clazz);
		}
	}
}
