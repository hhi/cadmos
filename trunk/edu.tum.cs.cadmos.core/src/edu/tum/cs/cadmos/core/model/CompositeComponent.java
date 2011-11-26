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

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

/**
 * A composite component has a behavior, which is defined by the combined
 * behavior of all of its children components. In particular, a composite
 * component does not have any machine specifications or variables like an
 * {@link IAtomicComponent}.
 * <p>
 * This is the reference implementation of the {@link ICompositeComponent}
 * interface.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class CompositeComponent extends AbstractComponent implements
		ICompositeComponent {

	/** The children components. */
	private final IListSet<IComponent> children = new ListSet<>();

	/**
	 * Creates a new CompositeComponent with the given <i>id</i>, <i>name</i>
	 * and <i>parent</i>.
	 * 
	 * @see CompositeComponent#CompositeComponent(String, ICompositeComponent)
	 */
	public CompositeComponent(String id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	/**
	 * Creates a new CompositeComponent with the given <i>id</i> and
	 * <i>parent</i>.
	 * <p>
	 * This constructor is mainly intended for testing purposes.
	 * 
	 * @see CompositeComponent#CompositeComponent(String, String,
	 *      ICompositeComponent)
	 */
	public CompositeComponent(String id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IComponent> getChildren() {
		return children;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * All children components and the ports- and channels-network between them
	 * are recursively cloned.
	 */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final ICompositeComponent clone = new CompositeComponent(getId(),
				getName(), newParent);
		clonePorts(clone);
		/* Clone the children components. */
		for (final IComponent child : getChildren()) {
			child.clone(clone);
		}
		/*
		 * Rewire the clone with its cloned children components and the cloned
		 * children with each other.
		 */
		final IListSet<IComponent> cloneChildren = clone.getChildren();
		for (final IComponent child : getChildren()) {
			final IComponent cloneChild = cloneChildren.get(child);
			for (final IPort dstPort : child.getInbound()) {
				final IPort srcPort = dstPort.getIncomingOppositePort();
				final IPort cloneSrcPort;
				if (srcPort.getComponent() == this) {
					cloneSrcPort = clone.getInbound().get(srcPort);
				} else {
					final IComponent cloneSrcComponent = cloneChildren
							.get(srcPort.getComponent());
					cloneSrcPort = cloneSrcComponent.getOutbound().get(srcPort);
				}
				final IPort cloneDstPort = cloneChild.getInbound().get(dstPort);
				dstPort.getIncoming().clone(cloneSrcPort, cloneDstPort);
			}
		}
		for (final IPort dstPort : getOutbound()) {
			final IPort srcPort = dstPort.getIncomingOppositePort();
			final IPort cloneDstPort = clone.getOutbound().get(dstPort);
			assertTrue(
					cloneChildren.contains(srcPort.getComponent()),
					"Expected cloneChildren to contain component '%s' of incoming opposite port",
					srcPort.getComponent());
			final IComponent cloneSrcComponent = cloneChildren.get(srcPort
					.getComponent());
			final IPort cloneSrcPort = cloneSrcComponent.getOutbound().get(
					srcPort);
			dstPort.getIncoming().clone(cloneSrcPort, cloneDstPort);
		}

		return clone;
	}
}
