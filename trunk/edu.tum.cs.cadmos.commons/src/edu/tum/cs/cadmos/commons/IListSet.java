package edu.tum.cs.cadmos.commons;

import java.util.Set;

public interface IListSet<E extends IIdentifiable> extends IListCollection<E> {

	E get(E element);

	E get(String id);

	Set<E> toSet();

}
