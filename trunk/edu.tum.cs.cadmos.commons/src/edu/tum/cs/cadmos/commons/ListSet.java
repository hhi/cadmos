package edu.tum.cs.cadmos.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListSet<E extends IIdentifiable> implements IListSet<E> {

	private final List<E> list = new ArrayList<>();

	private final Map<String, E> map = new HashMap<>();

	public ListSet() {
		// Default constructor.
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
	public Iterator<E> iterator() {
		return list.iterator();
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
	public void addAll(IListSet<E> elements) {
		for (final E e : elements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addAll(Collection<E> elements) {
		for (final E e : elements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(E element) {
		return contains(element.getId());
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(String id) {
		return map.containsKey(id);
	}

	/** {@inheritDoc} */
	@Override
	public E get(E element) {
		return get(element.getId());
	}

	/** {@inheritDoc} */
	@Override
	public E get(String id) {
		return map.get(id);
	}

	/** {@inheritDoc} */
	@Override
	public E get(int index) {
		return list.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public E getFirst() {
		return list.get(0);
	}

	/** {@inheritDoc} */
	@Override
	public E getLast() {
		return list.get(list.size() - 1);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return list.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other) {
		return other instanceof ListSet<?>
				&& ((ListSet<?>) other).map.keySet().equals(map.keySet());
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int h = 0;
		for (final E e : list) {
			h += e.getId().hashCode();
		}
		return h;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return list.toString();
	}

	/** {@inheritDoc} */
	@Override
	public List<E> toList() {
		return new ArrayList<>(list);
	}

	/** {@inheritDoc} */
	@Override
	public Set<E> toSet() {
		return new HashSet<>(map.values());
	}

}
