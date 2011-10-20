package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.utils.Assert.assertNotNull;
import edu.tum.cs.cadmos.core.types.IType;

public class Variable extends AbstractElement implements IVariable {

	private final IType type;

	public Variable(Object id, String name, IType type) {
		super(id, name);
		assertNotNull(type, "type");
		this.type = type;
	}

	public Variable(Object id, IType type) {
		this(id, null, type);
	}

	@Override
	public IType getType() {
		return type;
	}

}
