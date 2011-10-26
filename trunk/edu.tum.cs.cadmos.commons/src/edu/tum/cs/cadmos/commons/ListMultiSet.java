package edu.tum.cs.cadmos.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMultiSet<E extends IIdentifiable> extends
		AbstractListCollection<E, List<E>> implements IListMultiSet<E> {

	public ListMultiSet() {
		// Default constructor.
	}

	public ListMultiSet(Collection<E> initialElements) {
		addAll(initialElements);
	}

	public ListMultiSet(IListCollection<E, ?> initialElements) {
		addAll(initialElements);
	}

	@SafeVarargs
	public ListMultiSet(E... initialElements) {
		for (final E e : initialElements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void add(E element) {
		List<E> multiElements = map.get(element.getId());
		if (multiElements == null) {
			multiElements = new ArrayList<>();
			map.put(element.getId(), multiElements);
		}
		multiElements.add(element);
		list.add(element);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<E>> toMap() {
		final HashMap<String, List<E>> copy = new HashMap<>();
		for (final Map.Entry<String, List<E>> entry : map.entrySet()) {
			copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
		return copy;
	}

}
