package edu.tum.cs.cadmos.commons;

import java.util.List;

public interface IListMultiSet<E extends IIdentifiable> extends
		IListCollection<E> {

	List<E> get(E element);

	List<E> get(String id);

}
