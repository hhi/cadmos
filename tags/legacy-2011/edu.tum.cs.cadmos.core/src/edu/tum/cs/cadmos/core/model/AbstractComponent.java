/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

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

/**
 * This is the abstract reference implementation of the {@link IComponent}
 * interface, which serves as base class for the {@link AtomicComponent} and
 * {@link CompositeComponent} implementations.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
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