package edu.tum.cs.cadmos.core.types;

import static edu.tum.cs.cadmos.core.utils.Assert.assertNotNull;

public abstract class AbstractType implements IType {

	private final EType type;

	public AbstractType(EType type) {
		assertNotNull(type, "type");
		this.type = type;
	}

	@Override
	public EType getType() {
		return type;
	}

}
