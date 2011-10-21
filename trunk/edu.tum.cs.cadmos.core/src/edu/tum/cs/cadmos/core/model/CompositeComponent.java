package edu.tum.cs.cadmos.core.model;

import java.util.HashSet;
import java.util.Set;

public class CompositeComponent extends AbstractComponent implements
		ICompositeComponent {

	private final Set<IComponent> children = new HashSet<>();

	public CompositeComponent(String id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public CompositeComponent(String id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	@Override
	public Set<IComponent> getChildren() {
		return children;
	}

}
