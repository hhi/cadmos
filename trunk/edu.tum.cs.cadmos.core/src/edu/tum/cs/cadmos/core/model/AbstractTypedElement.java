package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import edu.tum.cs.cadmos.core.types.IType;

public abstract class AbstractTypedElement extends AbstractElement implements
		ITypedElement {

	private final IType type;

	public AbstractTypedElement(String id, String name, IType type) {
		super(id, name);
		assertNotNull(type, "type");
		this.type = type;
	}

	@Override
	public IType getType() {
		return type;
	}

}
