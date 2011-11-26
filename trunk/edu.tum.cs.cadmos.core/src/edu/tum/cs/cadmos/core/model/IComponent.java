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

import edu.tum.cs.cadmos.commons.core.IElement;
import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;

/**
 * A component is part of a hierarchical data-flow system model and has a parent
 * and a set of inound and outbound ports. The incoming and outgoing channels of
 * these ports allow to connect this component to its parent-, sibling- or
 * children components.
 * <p>
 * Note that the parent of a component optionally can be <code>null</code> to
 * indicate that this is a root-level component.
 * <p>
 * The non-abstract instances of components implement the
 * {@link IAtomicComponent} and {@link ICompositeComponent} interfaces. This
 * architecture resembles the well-known <a
 * href="http://en.wikipedia.org/wiki/Composite_pattern"><i>composite
 * pattern</i></a>.
 * 
 * @see AbstractComponent
 * @see IAtomicComponent
 * @see ICompositeComponent
 * @see IPort
 * @see IChannel
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: 873587029FE8D954E7681BC406120A6D
 */
public interface IComponent extends IElement {

	/**
	 * Returns the parent component, which optionally can be <code>null</code>
	 * to indicate that this is a root-level component.
	 * <p>
	 * Note that if <code>a.getParent() == b</code> holds then
	 * <code>b.getChildren().contains(a)</code> must hold, too.
	 */
	ICompositeComponent getParent();

	/** Returns the set of inbound ports. */
	IListSet<IPort> getInbound();

	/** Returns the set of outbound ports. */
	IListSet<IPort> getOutbound();

	/**
	 * Returns the union multiset of all inbound and outbound ports.
	 * 
	 * @see #getInbound()
	 * @see #getOutbound()
	 * @see #getPorts(EPortDirection)
	 */
	IListMultiSet<IPort> getAllPorts();

	/**
	 * Returns the ports with the given direction.
	 * 
	 * @see #getInbound()
	 * @see #getOutbound()
	 * @see #getAllPorts()
	 * */
	IListSet<IPort> getPorts(EPortDirection direction);

	/** Returns the set of incoming channels of all inbound ports. */
	IListSet<IChannel> getInboundIncomingChannels();

	/** Returns the set of outgoing channels of all inbound ports. */
	IListSet<IChannel> getInboundOutgoingChannels();

	/** Returns the set of incoming channels of all outbound ports. */
	IListSet<IChannel> getOutboundIncomingChannels();

	/** Returns the set of outgoing channels of all outbound ports. */
	IListSet<IChannel> getOutboundOutgoingChannels();

	/**
	 * Returns the union set of all incoming and outgoing channels connected to
	 * all inbound and outbound ports.
	 * <p>
	 * If a channel is a self-loop, it is only contained once in the returned
	 * set.
	 * 
	 * @see #getInboundIncomingChannels()
	 * @see #getInboundOutgoingChannels()
	 * @see #getOutboundIncomingChannels()
	 * @see #getOutboundOutgoingChannels()
	 */
	IListSet<IChannel> getAllChannels();

	/**
	 * Returns a clone of this component within the given <i>newParent</i>.
	 * <p>
	 * <b>Important:</b> inbound and outbound ports are cloned, but their
	 * respective channels are not contained in the returned clone and have to
	 * be created and rewired manually.
	 * 
	 */
	IComponent clone(ICompositeComponent newParent);

	/**
	 * Clones all ports of this component and creates them as members of the
	 * given <i>newComponent</i>.
	 * <p>
	 * This method should be used by concrete subclasses in their respective
	 * {@link #clone()} method implementations.
	 */
	void clonePorts(IComponent newComponent);

}
