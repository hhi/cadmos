package edu.tum.cs.cadmos.lib.model;

public abstract class AbstractPort<M> implements IPort<M> {

	private final int rate;

	public AbstractPort(int rate) {
		this.rate = rate;
	}

	@Override
	public int getRate() {
		return rate;
	}

}
