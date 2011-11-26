package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.AbstractElement;
import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.commons.core.IConsistencyVerifier;
import edu.tum.cs.cadmos.commons.core.IIdentifiable;
import edu.tum.cs.cadmos.commons.core.IListCollection;
import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListMultiSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

public abstract class AbstractComponent extends AbstractElement implements
		IComponent, IConsistencyVerifier {

	/** The parent of this component, which can be <code>null</code>. */
	private final ICompositeComponent parent;

	/** The set of inbound ports. */
	protected final IListSet<IPort> inbound = new ListSet<>(this);

	/** The set of outbound ports. */
	protected final IListSet<IPort> outbound = new ListSet<>(this);

	/**
	 * Creates a new AbstractComponent with the given <i>id</i>, <i>name</i> and
	 * <i>parent</i>.
	 * <p>
	 * If the parent is not <code>null</code>, this component adds itself
	 * automatically to its parent's set of children.
	 */
	public AbstractComponent(String id, String name, ICompositeComponent parent) {
		super(id, name);
		this.parent = parent;
		if (parent != null) {
			parent.getChildren().add(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public ICompositeComponent getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IPort> getInbound() {
		return inbound;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IPort> getOutbound() {
		return outbound;
	}

	/** {@inheritDoc} */
	@Override
	public IListMultiSet<IPort> getAllPorts() {
		final IListMultiSet<IPort> result = new ListMultiSet<>();
		result.addAll(getInbound());
		result.addAll(getOutbound());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IPort> getPorts(EPortDirection direction) {
		Assert.assertNotNull(direction, "direction");
		switch (direction) {
		case INBOUND:
			return getInbound();
		case OUTBOUND:
			return getOutbound();
		}
		throw new IllegalArgumentException("Unknown 'direction': '" + direction
				+ "'");
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getInboundIncomingChannels() {
		return ModelUtils.getIncoming(getInbound());
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getInboundOutgoingChannels() {
		return ModelUtils.getOutgoing(getInbound());
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getOutboundIncomingChannels() {
		return ModelUtils.getIncoming(getOutbound());
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getOutboundOutgoingChannels() {
		return ModelUtils.getOutgoing(getOutbound());
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getAllChannels() {
		final IListMultiSet<IPort> ports = getAllPorts();
		final IListSet<IChannel> result = ModelUtils.getOutgoing(ports);
		for (final IPort port : ports) {
			if (!result.contains(port.getIncoming())) {
				result.add(port.getIncoming());
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void clonePorts(IComponent newComponent) {
		for (final IPort port : getAllPorts()) {
			port.clone(newComponent);
		}
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
