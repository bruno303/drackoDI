package com.bso.drackodi.container;

import com.bso.drackodi.model.ClassInfo;
import com.bso.drackodi.scope.Scope;

import java.util.HashMap;
import java.util.Map;

public class ScopeMapClass {

    private final Map<Class<?>, ClassInfo> map = new HashMap<>();

    public synchronized <T> void putIfAbsent(Class<T> clazz, Scope scope) {
    	
    	ClassInfo classInfo = new ClassInfo(clazz, scope, clazz.getInterfaces());

        this.map.putIfAbsent(clazz, classInfo);
    }

	public synchronized Map<Class<?>, ClassInfo> getMap() {
		return map;
	}
	
	public synchronized <T> void putIfAbsent(Class<T> clazz, ClassInfo classInfo) {
        this.map.putIfAbsent(clazz, classInfo);
    }
}
