package edu.tum.cs.cadmos.core.types;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;

public class RecordMember {

	private final String id;

	private final IType type;

	public RecordMember(String id, IType type) {
		assertNotNull(id, "id");
		assertNotNull(type, "type");
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public IType getType() {
		return type;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof RecordMember
				&& ((RecordMember) other).id.equals(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
