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

package edu.tum.cs.cadmos.analysis.core;

import java.util.List;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.core.model.IAtomicComponent;
import edu.tum.cs.cadmos.core.model.IChannel;
import edu.tum.cs.cadmos.core.model.IComponent;

/**
 * The interface {@link INetwork} provides a set of wrapper methods for managing
 * a network of {@link IComponent}.
 * 
 * @author nvpopa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: 5AE6E5EDDABB33969CE765FB18B5F72E
 */
public interface INetwork {

	/**
	 * Returns a list of all atomic transitive successors from this
	 * {@link INetwork} of a given {@link IAtomicComponent}.
	 */
	public List<IAtomicComponent> getSuccessors(IAtomicComponent component);

	/**
	 * Returns a list of all atomic transitive predecessors from this
	 * {@link INetwork} of a given {@link IAtomicComponent}.
	 */
	public List<IAtomicComponent> getPredecessors(IAtomicComponent component);

	/**
	 * Returns a list of all channels in this {@link INetwork} that have as
	 * source <code>c1</code> and as destination <code>c2</code>.
	 */
	public List<IChannel> getChannels(IAtomicComponent c1, IAtomicComponent c2);

	/**
	 * Returns a list of all {@link IAtomicComponent} which are contained in
	 * this {@link INetwork}.
	 */
	public IListSet<IAtomicComponent> getAllComponents();

	/**
	 * Returns a list of all {@link IChannel} which are contained in this
	 * {@link INetwork}.
	 */
	public IListSet<IChannel> getAllChannels();
}
