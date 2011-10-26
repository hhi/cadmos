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

import edu.tum.cs.cadmos.commons.IListSet;

/**
 * A component in a hierarchical data-flow system model, which has a parent and
 * a set of incoming and outgoing channels. The incoming and outgoing channels
 * allow to connect this component to sibling components that have the same
 * parent.
 * <p>
 * Note that the parent optionally can be <code>null</code> to indicate that
 * this is a root-level component.
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
	 */
	ICompositeComponent getParent();

	/**
	 * Returns the set of incoming channels, that is, for each channel
	 * <code>c</code> in incoming <code>c.getDst().equals(this)</code> holds.
	 */
	IListSet<IChannel> getIncoming();

	/**
	 * Returns the set of outgoing channels, that is, for each channel
	 * <code>c</code> in outgoing <code>c.getSrc().equals(this)</code> holds.
	 */
	IListSet<IChannel> getOutgoing();

	/**
	 * Returns a clone of this component within the given <i>newParent</i>.
	 * <p>
	 * <b>Important:</b> incoming and outgoing channels are not contained in the
	 * returned clone and have to be rewired manually.
	 * 
	 */
	IComponent clone(ICompositeComponent newParent);

}
