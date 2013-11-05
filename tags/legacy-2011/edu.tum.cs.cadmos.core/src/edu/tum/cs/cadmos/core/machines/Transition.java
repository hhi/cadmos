package edu.tum.cs.cadmos.core.machines;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

public class Transition implements ITransition {

	private final IListSet<ICondition> preConditions = new ListSet<>();

	private final IListSet<ICondition> postConditions = new ListSet<>();

	@Override
	public IListSet<ICondition> getPreConditions() {
		return preConditions;
	}

	@Override
	public IListSet<ICondition> getPostConditions() {
		return postConditions;
	}

}
