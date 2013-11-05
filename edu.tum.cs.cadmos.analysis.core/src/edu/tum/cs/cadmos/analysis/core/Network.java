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

import java.util.ArrayList;
import java.util.List;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.commons.core.IListMultiSet;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListMultiSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.model.IAtomicComponent;
import edu.tum.cs.cadmos.core.model.IChannel;

/**
 * A implementation of a {@link INetwork} used in the methods of
 * {@link NetworkAnalysis}.
 * 
 * @author nvpopa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: C4487B555CBDD4E18BA76C8D34C4161B
 */
public class Network implements INetwork {

	/** The network of {@link IAtomicComponent}'s which is analyzed. */
	private final IListSet<IAtomicComponent> network;

	/** Creates a empty new Network. */
	public Network() {
		network = new ListSet<>();
	}

	/**
	 * Creates a new Network given its system boundary as a {@link IComponent}.
	 */
	// public Network(IComponent systemBoundary) {
	// network = ModelUtils.transformAtomicComponentNetwork(systemBoundary);
	// }

	/** {@inheritDoc} */
	@Override
	public List<IAtomicComponent> getSuccessors(IAtomicComponent component) {
		Assert.assertTrue(network.contains(component),
				"The given component %s is not contained in this network!",
				component);
		final List<IAtomicComponent> successors = new ArrayList<>();

		// for (final IChannel ch : component.getOutbound()) {
		// Assert.assertInstanceOf(ch.getDst(), IAtomicComponent.class,
		// "component");
		// final IAtomicComponent dst = (IAtomicComponent) ch.getDst();
		// successors.add(dst);
		// }
		return successors;
	}

	/** {@inheritDoc} */
	@Override
	public List<IAtomicComponent> getPredecessors(IAtomicComponent component) {
		Assert.assertTrue(network.contains(component),
				"The given component %s is not contained in this network!",
				component);
		final List<IAtomicComponent> predecessors = new ArrayList<>();
		// for (final IChannel ch : component.getInbound()) {
		// Assert.assertInstanceOf(ch.getSrc(), IAtomicComponent.class,
		// "component");
		// final IAtomicComponent src = (IAtomicComponent) ch.getSrc();
		// predecessors.add(src);
		// }
		return predecessors;
	}

	/** {@inheritDoc} */
	@Override
	public List<IChannel> getChannels(IAtomicComponent c1, IAtomicComponent c2) {
		Assert.assertTrue(network.contains(c1) && network.contains(c2),
				"Both components %s, %s have to be in this network!", c1, c2);
		final List<IChannel> channels = new ArrayList<>();
		// for (final IChannel ch : c1.getOutbound()) {
		// if (ch.getDst().equals(c2)) {
		// channels.add(ch);
		// }
		// }
		return channels;
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IAtomicComponent> getAllComponents() {
		return network;
	}

	/** {@inheritDoc} */
	@Override
	public IListMultiSet<IChannel> getAllChannels() {
		final IListMultiSet<IChannel> channels = new ListMultiSet<>();

		// for (final IAtomicComponent component : network) {
		// for (final IChannel och : component.getOutbound()) {
		// channels.add(och);
		// }
		// for (final IChannel ich : component.getInbound()) {
		// if (ich.getSrc() == null) {
		// channels.add(ich);
		// }
		// }
		// }

		return channels;
	}

	/** {@inheritDoc} */
	@Override
	public void addComponent(IAtomicComponent component) {
		network.add(component);
	}

}
