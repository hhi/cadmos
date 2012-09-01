package edu.tum.cs.cadmos.common;

public interface Predicate<T> {

	boolean holdsFor(T element);

}
