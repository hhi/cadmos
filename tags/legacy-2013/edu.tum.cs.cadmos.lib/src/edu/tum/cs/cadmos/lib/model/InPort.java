package edu.tum.cs.cadmos.lib.model;

public class InPort<M> extends AbstractPort<M> {

	public InPort() {
		this(1);
	}

	public InPort(int rate) {
		super(rate);
	}

	public M getCurrent() {
		return getStream().get(getStream().getLength() - 1);
	}

	public InStream<M> getStream() {
		// TODO: implement
		return null;
	}

}
