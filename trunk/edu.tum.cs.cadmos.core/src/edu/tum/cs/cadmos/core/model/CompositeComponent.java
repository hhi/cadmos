package edu.tum.cs.cadmos.core.model;

import java.util.Collection;
import java.util.LinkedHashSet;

public class CompositeComponent extends AbstractComponent implements
		ICompositeComponent {

	private final Collection<IComponent> children = new LinkedHashSet<>();

	public CompositeComponent(Object id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public CompositeComponent(Object id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	@Override
	public Collection<IComponent> getChildren() {
		return children;
	}

}
