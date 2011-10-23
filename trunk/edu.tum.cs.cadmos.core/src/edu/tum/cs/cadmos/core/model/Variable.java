package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import edu.tum.cs.cadmos.core.types.IType;
import edu.tum.cs.cadmos.core.types.VoidType;

public class Variable extends AbstractTypedElement implements IVariable {

	private final IAtomicComponent scope;

	public Variable(String id, String name, IType type, IAtomicComponent scope) {
		super(id, name, type);
		assertNotNull(scope, "component");
		assertTrue(!scope.getVariables().contains(getId()),
				"Variable with id '%s' is present in '%s' already", id, scope);
		assertTrue(
				!scope.getIncoming().contains(getId()),
				"Channel with id '%s' is present in incoming channels of '%s' already",
				id, scope);
		assertTrue(
				!scope.getOutgoing().contains(getId()),
				"Channel with id '%s' is present in outgoing channels of '%s' already",
				id, scope);
		this.scope = scope;
		scope.getVariables().add(this);
	}

	public Variable(String id, IType type, IAtomicComponent component) {
		this(id, null, type, component);
	}

	public Variable(String id, IAtomicComponent component) {
		this(id, null, new VoidType(), component);
	}

	/** {@inheritDoc} */
	@Override
	public IAtomicComponent getScope() {
		return scope;
	}

	/** {@inheritDoc} */
	@Override
	public IVariable clone(IAtomicComponent newScope) {
		return new Variable(getId(), getName(), getType(), newScope);
	}

}
