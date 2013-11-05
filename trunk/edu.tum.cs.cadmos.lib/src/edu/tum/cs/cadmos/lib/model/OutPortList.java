package edu.tum.cs.cadmos.lib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class OutPortList<M> implements Iterable<OutPort<M>> {

	private final List<OutPort<M>> list;

	public OutPortList(int multiplicity) {
		this(multiplicity, 1);
	}

	public OutPortList(int multiplicity, int rate) {
		final List<OutPort<M>> list = new ArrayList<>(multiplicity);
		for (int i = 0; i < multiplicity; i++) {
			list.add(new OutPort<M>(rate));
		}
		this.list = Collections.unmodifiableList(list);
	}

	public OutPort<M> get(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	@Override
	public Iterator<OutPort<M>> iterator() {
		return list.iterator();
	}

}
