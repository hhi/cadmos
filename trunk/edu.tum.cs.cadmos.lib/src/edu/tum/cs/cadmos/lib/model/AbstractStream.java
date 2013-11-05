package edu.tum.cs.cadmos.lib.model;

import java.util.List;

public abstract class AbstractStream<M> implements IStream<M> {

	protected final List<M> messages;

	public AbstractStream(List<M> messages) {
		this.messages = messages;
	}

	@Override
	public int getLength() {
		return messages.size();
	}

}
