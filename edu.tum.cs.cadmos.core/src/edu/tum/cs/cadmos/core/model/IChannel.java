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

import java.util.List;

import edu.tum.cs.cadmos.commons.core.IElement;
import edu.tum.cs.cadmos.core.expressions.IExpression;

/**
 * A channel is directed and connects from a source to a destination port.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: 918F24F813D187B494D5ED1A3F260CC4
 */
public interface IChannel extends IElement {

	/** Returns the source port. */
	IPort getSrc();

	/** Returns the destination port. */
	IPort getDst();

	/**
	 * Returns the source component, which is the component that owns the source
	 * port.
	 */
	IComponent getSrcComponent();

	/**
	 * Returns the destination component, which is the component that owns the
	 * destination port.
	 */
	IComponent getDstComponent();

	/**
	 * Returns the number of messages that are buffered by this channel. The
	 * delay equals the number of initial messages provided.
	 * 
	 * @see #getInitialMessages()
	 */
	int getDelay();

	/**
	 * Returns the mutable list of initial messages. Messages transmitted over
	 * this channel are buffered and delayed by the number of initial messages.
	 * 
	 * @see #getDelay()
	 */
	List<IExpression> getInitialMessages();

	/** Returns the source's production rate. */
	int getSrcRate();

	/** Returns the destination's consumption rate. */
	int getDstRate();

	/**
	 * Returns a clone of this channel that connects from the given
	 * <i>newSrc</i> to the given <i>newDst</i>.
	 */
	IChannel clone(IPort newSrc, IPort newDst);

}
