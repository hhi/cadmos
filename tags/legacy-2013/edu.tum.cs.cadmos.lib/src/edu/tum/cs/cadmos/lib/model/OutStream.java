package edu.tum.cs.cadmos.lib.model;

import java.util.List;

public class OutStream<M> extends AbstractStream<M> {

	public OutStream(List<M> messages) {
		super(messages);
	}

	public void set(int index, M message) {
		messages.set(index, message);
	}

	public void set(InStream<M> stream) {
		for (int i = 0; i < stream.getLength(); i++) {
			set(i, stream.get(i));
		}
	}

	public void set(M message) {
		messages.set(messages.size() - 1, message);
	}

}
