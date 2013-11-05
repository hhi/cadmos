package edu.tum.cs.cadmos.lib.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class InPortList<M> implements Iterable<InPort<M>> {

	private final List<InPort<M>> list;

	public InPortList(int multiplicity) {
		this(multiplicity, 1);
	}

	public InPortList(int multiplicity, int rate) {
		final List<InPort<M>> list = new ArrayList<>(multiplicity);
		for (int i = 0; i < multiplicity; i++) {
			list.add(new InPort<M>(rate));
		}
		this.list = Collections.unmodifiableList(list);
	}

	public InPort<M> get(int index) {
		return list.get(index);
	}

	public int getSize() {
		return list.size();
	}

	@Override
	public Iterator<InPort<M>> iterator() {
		return list.iterator();
	}

}
