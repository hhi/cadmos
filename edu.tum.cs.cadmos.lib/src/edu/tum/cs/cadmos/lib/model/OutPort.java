package edu.tum.cs.cadmos.lib.model;

public class OutPort<M> extends AbstractPort<M> {

	public OutPort() {
		this(1);
	}

	public OutPort(int rate) {
		super(rate);
	}

	public void setCurrent(M message) {
		// TODO: implement
	}

	public void setStream(int deltaT, M message) {
		// TODO: implement
	}

}
