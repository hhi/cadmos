package edu.tum.cs.cadmos.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListSet<E extends IIdentifiable> implements IListSet<E> {

	private final List<E> list = new ArrayList<>();

	private final Map<String, E> map = new HashMap<>();

	public ListSet() {
		// Default constructor.
	}

	public ListSet(Collection<E> initialElements) {
		addAll(initialElements);
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public void add(E element) {
		if (map.containsKey(element.getId())) {
			throw new IllegalArgumentException("Element '" + element
					+ "' with id '" + element.getId() + "' is already present");
		}
		map.put(element.getId(), element);
		list.add(element);
	}

	@Override
	public void addAll(Collection<E> elements) {
		for (final E e : elements) {
			add(e);
		}
	}

	@Override
	public boolean contains(E element) {
		return contains(element.getId());
	}

	@Override
	public boolean contains(String id) {
		return map.containsKey(id);
	}

	@Override
	public E get(E element) {
		return get(element.getId());
	}

	@Override
	public E get(String id) {
		return map.get(id);
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E getFirst() {
		return list.get(0);
	}

	@Override
	public E getLast() {
		return list.get(list.size() - 1);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof ListSet<?>
				&& ((ListSet<?>) other).map.keySet().equals(map.keySet());
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (final E e : list) {
			h += e.getId().hashCode();
		}
		return h;
	}

}
