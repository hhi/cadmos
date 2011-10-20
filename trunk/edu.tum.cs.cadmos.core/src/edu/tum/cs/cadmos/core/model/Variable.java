package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import edu.tum.cs.cadmos.core.types.IType;

public class Variable extends AbstractVariable {

	public Variable(Object id, String name, IType type,
			IAtomicComponent component) {
		super(id, name, type);
		assertNotNull(component, "component");
		assertTrue(!component.getVariables().contains(this),
				"Variable with id '%s' is present in '%s' already", id,
				component);
		assertTrue(
				!component.getIncoming().contains(this),
				"Channel with id '%s' is present in incoming channels of '%s' already",
				id, component);
		assertTrue(
				!component.getOutgoing().contains(this),
				"Channel with id '%s' is present in outgoing channels of '%s' already",
				id, component);
		component.getVariables().add(this);
	}

	public Variable(Object id, IType type, IAtomicComponent component) {
		this(id, null, type, component);
	}

}
