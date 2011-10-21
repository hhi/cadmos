package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

public abstract class AbstractComponent extends AbstractElement implements
		IComponent {

	private final ICompositeComponent parent;

	private final IListSet<IChannel> incoming = new ListSet<>();

	private final IListSet<IChannel> outgoing = new ListSet<>();

	public AbstractComponent(String id, String name, ICompositeComponent parent) {
		super(id, name);
		this.parent = parent;
		if (parent != null) {
			assertTrue(!parent.getChildren().contains(getId()),
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
	public IListSet<IChannel> getIncoming() {
		return incoming;
	}

	@Override
	public IListSet<IChannel> getOutgoing() {
		return outgoing;
	}

}
