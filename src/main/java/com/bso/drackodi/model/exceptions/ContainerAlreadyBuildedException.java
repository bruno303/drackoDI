package com.bso.drackodi.model.exceptions;

public class ContainerAlreadyBuildedException extends DrackoDIException {

	public ContainerAlreadyBuildedException() {
		super("Container was builded already! Operations are not allowed anymore!");
	}
	
	public static void throwIf(boolean condition) {
		if (condition) {
			throw new ContainerAlreadyBuildedException();
		}
	}
}
