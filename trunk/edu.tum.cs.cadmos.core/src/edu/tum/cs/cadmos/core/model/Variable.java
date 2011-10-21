package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import edu.tum.cs.cadmos.core.types.IType;
import edu.tum.cs.cadmos.core.types.VoidType;

public class Variable extends AbstractVariable {

	public Variable(String id, String name, IType type,
			IAtomicComponent component) {
		super(id, name, type);
		assertNotNull(component, "component");
		assertTrue(!component.getVariables().contains(getId()),
				"Variable with id '%s' is present in '%s' already", id,
				component);
		assertTrue(
				!component.getIncoming().contains(getId()),
				"Channel with id '%s' is present in incoming channels of '%s' already",
				id, component);
		assertTrue(
				!component.getOutgoing().contains(getId()),
				"Channel with id '%s' is present in outgoing channels of '%s' already",
				id, component);
		component.getVariables().add(this);
	}

	public Variable(String id, IType type, IAtomicComponent component) {
		this(id, null, type, component);
	}

	public Variable(String id, IAtomicComponent component) {
		this(id, null, new VoidType(), component);
	}

}
