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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListMultiSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.types.IType;

/**
 * This is the reference implementation of the {@link IPort} interface.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class Port extends AbstractTypedElement implements IPort {

	private final IComponent component;

	private IChannel incoming;

	private final ListSet<IChannel> outgoing = new ListSet<>();

	private final EPortDirection direction;

	/**
	 * Creates a new port.
	 */
	public Port(String id, String name, IType type, IComponent component,
			EPortDirection direction) {
		super(id, name, type);
		assertNotNull(component, "component");
		assertNotNull(direction, "direction");
		this.component = component;
		this.direction = direction;
		switch (direction) {
		case INBOUND:
			component.getInbound().add(this);
			break;
		case OUTBOUND:
			component.getOutbound().add(this);
			break;
		}
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getComponent() {
		return component;
	}

	/** Returns the direction. */
	@Override
	public EPortDirection getDirection() {
		return direction;
	}

	/** {@inheritDoc} */
	@Override
	public IChannel getIncoming() {
		return incoming;
	}

	/** {@inheritDoc} */
	@Override
	public void setIncoming(IChannel incoming) {
		assertNotNull(incoming, "incoming");
		assertTrue(
				this.incoming == null,
				"Channel 'incoming' is set already to '%s', cannot set to '%s'",
				this.incoming, incoming);
		this.incoming = incoming;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IChannel> getOutgoing() {
		return outgoing;
	}

	/** {@inheritDoc} */
	@Override
	public IPort getIncomingOppositePort() {
		if (incoming != null) {
			return incoming.getSrc();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IListMultiSet<IPort> getOutgoingOppositePorts() {
		final IListMultiSet<IPort> result = new ListMultiSet<>();
		for (final IChannel channel : outgoing) {
			result.add(channel.getDst());
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getIncomingOppositeComponent() {
		final IPort port = getIncomingOppositePort();
		if (port != null) {
			return port.getComponent();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IComponent> getOutgoingOppositeComponents() {
		final IListSet<IComponent> result = new ListSet<>();
		for (final IPort port : getOutgoingOppositePorts()) {
			final IComponent component = port.getComponent();
			if (component != null) {
				if (!result.contains(component)) {
					result.add(component);
				}
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public IPort clone(IComponent newComponent) {
		return new Port(getId(), getName(), getType(), newComponent,
				getDirection());
	}

}
