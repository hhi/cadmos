package edu.tum.cs.cadmos.commons;

import java.util.Collection;
import java.util.RandomAccess;

public interface IListSet<E extends IIdentifiable> extends Iterable<E>,
		RandomAccess {

	void add(E element);

	void addAll(Collection<E> elements);

	boolean contains(String id);

	E get(String id);

	E get(int index);

	E getFirst();

	E getLast();

	int size();

	boolean isEmpty();

}
