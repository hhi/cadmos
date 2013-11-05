package edu.tum.cs.cadmos.lib.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SubComponentList<C> implements Iterable<C> {

	private final List<C> list;

	public SubComponentList(List<C> subComponents) {
		this.list = Collections.unmodifiableList(subComponents);
	}

	public C get(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	@Override
	public Iterator<C> iterator() {
		return list.iterator();
	}

}
