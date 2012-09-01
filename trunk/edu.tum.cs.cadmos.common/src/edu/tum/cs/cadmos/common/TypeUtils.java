package edu.tum.cs.cadmos.common;

public class TypeUtils {

	public static boolean instanceOfAny(Object object, Class<?>... classes) {
		final Class<? extends Object> objectClass = object.getClass();
		for (final Class<?> clazz : classes) {
			if (clazz.isAssignableFrom(objectClass)) {
				return true;
			}
		}
		return false;
	}

}
