package edu.tum.cs.cadmos.commons;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

public interface IListSet<E extends IIdentifiable> extends Iterable<E>,
		RandomAccess {

	void add(E element);

	void addAll(IListSet<E> elements);

	void addAll(Collection<E> elements);

	boolean contains(E element);

	boolean contains(String id);

	E get(E element);

	E get(String id);

	E get(int index);

	E getFirst();

	E getLast();

	int size();

	boolean isEmpty();

	List<E> toList();

	Set<E> toSet();

}
