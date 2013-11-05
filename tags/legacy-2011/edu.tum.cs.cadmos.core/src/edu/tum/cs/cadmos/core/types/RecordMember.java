package edu.tum.cs.cadmos.core.types;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import edu.tum.cs.cadmos.commons.core.AbstractIdentifiable;

public class RecordMember extends AbstractIdentifiable {

	private final IType type;

	public RecordMember(String id, IType type) {
		super(id);
		assertNotNull(type, "type");
		this.type = type;
	}

	public IType getType() {
		return type;
	}

}