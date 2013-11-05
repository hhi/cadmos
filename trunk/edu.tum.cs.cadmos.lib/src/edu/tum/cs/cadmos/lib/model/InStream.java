package edu.tum.cs.cadmos.lib.model;

import java.util.Iterator;
import java.util.List;

public class InStream<M> extends AbstractStream<M> implements Iterable<M> {

	public InStream(List<M> messages) {
		super(messages);
	}

	public M get(int index) {
		return messages.get(index);
	}

	@Override
	public Iterator<M> iterator() {
		return messages.iterator();
	}

}
