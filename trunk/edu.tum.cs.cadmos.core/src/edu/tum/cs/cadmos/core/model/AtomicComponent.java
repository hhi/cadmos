package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.machines.IMachine;

public class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	private final IListSet<IVariable> variables = new ListSet<>();

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
	public IListSet<IVariable> getVariables() {
		return variables;
	}

	@Override
	public IMachine getMachine() {
		return machine;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final IAtomicComponent clone = new AtomicComponent(getId(), getName(),
				newParent, getMachine());
		for (final IVariable variable : variables) {
			variable.clone(clone);
		}
		return clone;
	}

}
