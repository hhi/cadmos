package edu.tum.cs.cadmos.common;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	@SafeVarargs
	public static <E> List<E> concat(List<E>... lists) {
		final List<E> result = new ArrayList<>();
		for (final List<E> list : lists) {
			result.addAll(list);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <E, T extends E> List<T> filter(List<E> list, Class<T> clazz) {
		final List<T> result = new ArrayList<>();
		for (final E element : list) {
			if (clazz.isAssignableFrom(element.getClass())) {
				result.add((T) element);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <E, T extends E> List<T> filter(List<E> list,
			Predicate<E> predicate) {
		final List<T> result = new ArrayList<>();
		for (final E element : list) {
			if (predicate.holdsFor(element)) {
				result.add((T) element);
			}
		}
		return result;
	}
}
