package edu.tum.cs.cadmos.common;


public class StringUtils {

	public static String toFeatureName(String s) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < s.length() && Character.isUpperCase(s.charAt(i))) {
			// if prev is uc and next is lc then break: XYZSystem -> xyzSystem
			if (i > 0 && Character.isUpperCase(s.charAt(i - 1))
					&& i + 1 < s.length()
					&& Character.isLowerCase(s.charAt(i + 1))) {
				break;
			}
			sb.append(s.charAt(i++));
		}
		return sb.toString().toLowerCase() + s.substring(i);
	}

	public static String toCCAbbreviatedFeatureName(String s) {
		if (s == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (Character.isUpperCase(s.charAt(i))) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString().toLowerCase();
	}

}
