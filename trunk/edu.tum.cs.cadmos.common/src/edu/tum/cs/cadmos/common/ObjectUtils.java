package edu.tum.cs.cadmos.common;

public class ObjectUtils {

	public static int nullsafeHashcode(Object object) {
		return object == null ? 0 : object.hashCode();
	}

	public static boolean nullsafeEquals(Object a, Object b) {
		return a == b || a != null && b != null && a.equals(b);
	}

}
