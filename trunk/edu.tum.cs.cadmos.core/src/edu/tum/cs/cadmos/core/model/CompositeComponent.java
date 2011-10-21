package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

public class CompositeComponent extends AbstractComponent implements
		ICompositeComponent {

	private final IListSet<IComponent> children = new ListSet<>();

	public CompositeComponent(String id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public CompositeComponent(String id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	@Override
	public IListSet<IComponent> getChildren() {
		return children;
	}

}
