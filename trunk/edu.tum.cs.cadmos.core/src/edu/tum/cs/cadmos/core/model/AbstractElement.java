package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;

public abstract class AbstractElement implements IElement {

	private final String id;

	private final String name;

	public AbstractElement(String id, String name) {
		assertNotNull(id, "id");
		this.id = id;
		this.name = name != null ? name : id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof IElement)
				&& ((IElement) other).getId().equals(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + id + ")";
	}

}
