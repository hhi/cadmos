package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.AbstractIdentifiable;

public abstract class AbstractElement extends AbstractIdentifiable implements
		IElement {

	private final String name;

	public AbstractElement(String id, String name) {
		super(id);
		this.name = (name == null) ? getId() : name;
	}

	@Override
	public String getName() {
		return name;
	}

}
