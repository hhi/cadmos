package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.core.utils.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractComponent extends AbstractElement implements
		IComponent {

	private final ICompositeComponent parent;

	private final Set<IChannel> incoming = new HashSet<>();

	private final Set<IChannel> outgoing = new HashSet<>();

	public AbstractComponent(Object id, String name, ICompositeComponent parent) {
		super(id, name);
		this.parent = parent;
		if (parent != null) {
			assertTrue(!parent.getChildren().contains(this),
					"Component with id '%s' is present in parent '%s' already",
					getId(), parent);
			parent.getChildren().add(this);
		}
	}

	@Override
	public ICompositeComponent getParent() {
		return parent;
	}

	@Override
	public Set<IChannel> getIncoming() {
		return incoming;
	}

	@Override
	public Set<IChannel> getOutgoing() {
		return outgoing;
	}

}
