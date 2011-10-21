package edu.tum.cs.cadmos.commons;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;

public abstract class AbstractIdentifiable implements IIdentifiable {

	private final String id;

	public AbstractIdentifiable(String id) {
		assertNotNull(id, "id");
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof IIdentifiable)
				&& ((IIdentifiable) other).getId().equals(getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ")";
	}

}
