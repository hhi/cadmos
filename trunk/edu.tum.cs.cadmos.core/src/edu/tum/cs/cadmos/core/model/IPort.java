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

import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;

/**
 * A port belongs to a {@link IComponent} and represents the typed source- or
 * destination connection point of a {@link IChannel}.
 * <p>
 * A port can either have inbound or outbound direction:<br>
 * <b>Inbound ports.</b> If a port is inbound, it is the destination of one
 * incoming channel from the parent or sibling level and the source of one or
 * more outgoing channels to the children level. If a port has no outgoing
 * channels, the port is a direct input of the component's machine.<br>
 * <b>Outbound ports.</b> If a port is outbound, it is the destination of one
 * channel from the children level and the source of one or more outgoing
 * channels to the parent or sibling level. If the port has no incoming channel,
 * the port is a direct output of the component's machine.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public interface IPort extends ITypedElement {

	/** Returns the component that owns this port. */
	IComponent getComponent();

	/** Returns the direction of this port, which is either inbound or outbound. */
	EPortDirection getDirection();

	/**
	 * Returns the single incoming channel of this port, which can be
	 * <code>null</code>.
	 */
	IChannel getIncoming();

	/** Sets the single incoming channel of this port. */
	void setIncoming(IChannel incoming);

	/** Returns the outgoing channels of this port. */
	IListSet<IChannel> getOutgoing();

	/**
	 * Returns the opposite port of the incoming channel, which is the source of
	 * the incoming channel.
	 */
	IPort getIncomingOppositePort();

	/**
	 * Returns the opposite ports of the outgoing channels, which are the
	 * destinations of the outgoing channels.
	 */
	IListMultiSet<IPort> getOutgoingOppositePorts();

	/**
	 * Returns the component that is on the source side of the incoming channel.
	 */
	IComponent getIncomingOppositeComponent();

	/**
	 * Returns the union of components that are on the destinations sides of the
	 * outgoing channels.
	 */
	IListSet<IComponent> getOutgoingOppositeComponents();

	/**
	 * Clones this port and adds it to the given <i>new component</i>.
	 * <p>
	 * Note that the incoming and outgoing channels are not cloned and need to
	 * be rewired manually.
	 */
	IPort clone(IComponent newComponent);

}
