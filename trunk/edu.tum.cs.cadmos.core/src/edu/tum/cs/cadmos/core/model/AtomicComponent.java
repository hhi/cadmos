package edu.tum.cs.cadmos.core.model;

import java.util.HashSet;
import java.util.Set;

import edu.tum.cs.cadmos.core.machines.IMachine;

public class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	private final Set<IVariable> variables = new HashSet<>();

	private final IMachine machine;

	public AtomicComponent(String id, String name, ICompositeComponent parent,
			IMachine machine) {
		super(id, name, parent);
		this.machine = machine;
	}

	public AtomicComponent(String id, ICompositeComponent parent) {
		this(id, null, parent, null);
	}

	@Override
	public Set<IVariable> getVariables() {
		return variables;
	}

	@Override
	public IMachine getMachine() {
		return machine;
	}

}
