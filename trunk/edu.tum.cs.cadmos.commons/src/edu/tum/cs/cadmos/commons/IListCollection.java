package edu.tum.cs.cadmos.commons;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

public interface IListCollection<E extends IIdentifiable, R> extends
		Iterable<E>, RandomAccess {

	void add(E element);

	void addAll(IListCollection<E, ?> elements);

	void addAll(Collection<E> elements);

	boolean contains(E element);

	boolean contains(String id);

	R get(E element);

	R get(String id);

	E get(int index);

	E getFirst();

	E getLast();

	int size();

	boolean isEmpty();

	List<E> toList();

	Set<R> toSet();

	Map<String, R> toMap();

}
