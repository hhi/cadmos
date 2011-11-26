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

	IComponent getComponent();

	EPortDirection getDirection();

	IChannel getIncoming();

	void setIncoming(IChannel incoming);

	IListSet<IChannel> getOutgoing();

	IPort getIncomingOppositePort();

	IListMultiSet<IPort> getOutgoingOppositePorts();

	IComponent getIncomingOppositeComponent();

	IListSet<IComponent> getOutgoingOppositeComponents();

	IPort clone(IComponent newComponent);

}
