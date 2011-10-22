package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import edu.tum.cs.cadmos.core.types.IType;

public abstract class AbstractVariable extends AbstractElement implements
		IVariable {

	private final IType type;

	public AbstractVariable(String id, String name, IType type) {
		super(id, name);
		assertNotNull(type, "type");
		this.type = type;
	}

	@Override
	public IType getType() {
		return type;
	}

}