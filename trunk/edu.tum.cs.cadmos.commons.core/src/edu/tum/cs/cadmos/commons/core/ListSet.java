package edu.tum.cs.cadmos.commons.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListSet<E extends IIdentifiable> extends
		AbstractListCollection<E, E> implements IListSet<E> {

	public ListSet() {
		/* Default constructor. */
	}

	public ListSet(Collection<E> initialElements) {
		addAll(initialElements);
	}

	public ListSet(IListSet<E> initialElements) {
		addAll(initialElements);
	}

	@SafeVarargs
	public ListSet(E... initialElements) {
		for (final E e : initialElements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void add(E element) {
		if (map.containsKey(element.getId())) {
			throw new IllegalArgumentException("Element '" + element
					+ "' with id '" + element.getId() + "' is already present");
		}
		map.put(element.getId(), element);
		list.add(element);
	}

	/** {@inheritDoc} */
	@Override
	public Set<E> toSet() {
		return new HashSet<>(map.values());
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, E> toMap() {
		return new HashMap<>(map);
	}

}