package edu.tum.cs.cadmos.commons;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public interface IListCollection<E extends IIdentifiable> extends Iterable<E>,
		RandomAccess {

	void add(E element);

	void addAll(IListCollection<E> elements);

	void addAll(Collection<E> elements);

	boolean contains(E element);

	boolean contains(String id);

	E get(int index);

	E getFirst();

	E getLast();

	int size();

	boolean isEmpty();

	List<E> toList();

}
