package edu.tum.cs.cadmos.commons.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListSet<E extends IIdentifiable> extends
		AbstractListCollection<E, E> implements IListSet<E> {

	public ListSet() {
		super(null);
	}

	public ListSet(IConsistencyVerifier consistencyVerifier) {
		super(consistencyVerifier);
	}

	public ListSet(Collection<E> initialElements) {
		super(null);
		addAll(initialElements);
	}

	public ListSet(IListSet<E> initialElements) {
		super(null);
		addAll(initialElements);
	}

	@SafeVarargs
	public ListSet(E... initialElements) {
		super(null);
		for (final E e : initialElements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void add(E element) {
		verifyConsistentAdd(element);
		if (map.containsKey(element.getId())) {
			throw new IllegalArgumentException("Element '" + element
					+ "' with id '" + element.getId() + "' is already present");
		}
		map.put(element.getId(), element);
		list.add(element);
	}

	/** {@inheritDoc} */
	@Override
	public E get(String id) {
		return map.get(id);
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
