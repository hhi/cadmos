package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;

public abstract class AbstractElement implements IElement {

	private final Object id;

	private final String name;

	public AbstractElement(Object id, String name) {
		assertNotNull(id, "id");
		this.id = id;
		this.name = name != null ? name : id.toString();
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof AbstractElement)
				&& ((AbstractElement) other).id.equals(id);
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
