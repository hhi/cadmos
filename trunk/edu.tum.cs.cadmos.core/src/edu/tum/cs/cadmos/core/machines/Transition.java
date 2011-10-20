package edu.tum.cs.cadmos.core.machines;

import java.util.HashSet;
import java.util.Set;

public class Transition implements ITransition {

	private final Set<ICondition> preConditions = new HashSet<>();

	private final Set<ICondition> postConditions = new HashSet<>();

	@Override
	public Set<ICondition> getPreConditions() {
		return preConditions;
	}

	@Override
	public Set<ICondition> getPostConditions() {
		return postConditions;
	}

}
