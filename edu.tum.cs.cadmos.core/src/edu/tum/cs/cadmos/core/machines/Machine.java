package edu.tum.cs.cadmos.core.machines;

import java.util.ArrayList;
import java.util.List;

public class Machine implements IMachine {

	private final List<ITransition> transitions = new ArrayList<>();

	@Override
	public List<ITransition> getTransitions() {
		return transitions;
	}

}
