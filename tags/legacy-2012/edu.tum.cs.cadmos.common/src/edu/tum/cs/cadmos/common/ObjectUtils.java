package edu.tum.cs.cadmos.common;

public class ObjectUtils {

	/**
	 * Returns <code>true</code> if <i>a</i> equals <i>b</i> and returns
	 * <code>false</code> otherwise, while the interpretation
	 * <code>(null == null) = true</code> holds.
	 */
	public static boolean equalsInterpretNullAsDefinedValue(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

	/**
	 * Returns <code>true</code> if <i>a</i> equals <i>b</i> and returns
	 * <code>false</code> otherwise, while the interpretation
	 * <code>(null == null) = false</code> holds.
	 */
	public static boolean equalsInterpretNullAsUndefinedValue(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}

}
