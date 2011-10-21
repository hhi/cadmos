package edu.tum.cs.cadmos.core.model;

import java.util.Collections;
import java.util.Set;

public class ModelUtils {

	public static <E extends IElement> E find(Object id, Set<E> set) {
		for (final E e : set) {
			if (e.getId().equals(id)) {
				return e;
			}
		}
		return null;
	}

	public static Set<IComponent> dissolveComponent(IComponent component) {
		return Collections.EMPTY_SET;
	}

}
