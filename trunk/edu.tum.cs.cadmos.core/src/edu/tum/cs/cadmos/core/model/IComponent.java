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
 * A component of a hierarchical data-flow system model, which has a parent that
 * optionally can be <code>null</code> and a set of incoming and outgoing
 * channels.
 * <p>
 * The non-abstract instances of components implement the
 * {@link IAtomicComponent} and {@link ICompositeComponent} interfaces. This
 * architecture resembles the well-known <i>composite pattern</i>.
 * 
 * @see AbstractComponent
 * @see IAtomicComponent
 * @see ICompositeComponent
 * @see IChannel
 * 
 * @author wolfgang.schwitzer
 * @author $Author$
 * @version $Rev$
 * @ConQAT.Rating RED Hash:
 */
public interface IComponent extends IElement {

	ICompositeComponent getParent();

	IListSet<IChannel> getIncoming();

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
