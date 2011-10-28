package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotContainedIn;
import edu.tum.cs.cadmos.commons.core.IIdentifiable;
import edu.tum.cs.cadmos.commons.core.IListCollection;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.machines.IMachine;

public class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	private final IListSet<IVariable> variables = new ListSet<>(this);

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

	/** {@inheritDoc} */
	@Override
	public void verifyConsistentAdd(IListCollection<?, ?> collection,
			IIdentifiable element) throws AssertionError {
		if (collection == incoming || collection == outgoing) {
			assertNotContainedIn(element, variables, "channel", "variables");
		} else if (collection == variables) {
			assertNotContainedIn(element, incoming, "variable", "incoming");
			assertNotContainedIn(element, outgoing, "variable", "outgoing");
		}
	}

}
