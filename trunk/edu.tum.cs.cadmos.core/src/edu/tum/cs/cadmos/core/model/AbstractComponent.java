package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.AbstractElement;
import edu.tum.cs.cadmos.commons.core.IConsistencyVerifier;
import edu.tum.cs.cadmos.commons.core.IIdentifiable;
import edu.tum.cs.cadmos.commons.core.IListCollection;
import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListMultiSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

public abstract class AbstractComponent extends AbstractElement implements
		IComponent, IConsistencyVerifier {

	private final ICompositeComponent parent;

	protected final IListSet<IChannel> incoming = new ListSet<>(this);

	protected final IListMultiSet<IChannel> outgoing = new ListMultiSet<>(this);

	public AbstractComponent(String id, String name, ICompositeComponent parent) {
		super(id, name);
		this.parent = parent;
		if (parent != null) {
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
	public IListMultiSet<IChannel> getOutgoing() {
		return outgoing;
	}

	/**
	 * {@inheritDoc}.
	 * <p>
	 * The default implementation does not perform any consistency checks,
	 * hence, subclasses should override.
	 */
	@Override
	public void verifyConsistentAdd(IListCollection<?, ?> collection,
			IIdentifiable element) throws AssertionError {
		/* Default implementation does not perform any checks. */
	}

}
