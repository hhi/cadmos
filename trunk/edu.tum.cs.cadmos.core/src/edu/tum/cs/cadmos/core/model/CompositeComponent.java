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
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

/**
 * A composite component has a behavior defined the combined behavior of some
 * children components. In particular, a composite component does not have any
 * machine specifications or variables like an {@link IAtomicComponent}.
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

	private final IListSet<IComponent> children = new ListSet<>();

	public CompositeComponent(String id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public CompositeComponent(String id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	@Override
	public IListSet<IComponent> getChildren() {
		return children;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final ICompositeComponent clone = new CompositeComponent(getId(),
				getName(), newParent);
		/* Clone the children components. */
		for (final IComponent child : getChildren()) {
			child.clone(clone);
		}
		/* Rewire the cloned children components. */
		final IListSet<IComponent> cloneChildren = clone.getChildren();
		for (final IComponent child : getChildren()) {
			for (final IChannel c : child.getIncoming()) {
				if (c.getSrc() == null) {
					assertNotNull(c.getDst(), "c.getDst()");
					final IComponent newDst = cloneChildren.get(c.getDst());
					c.clone(null, newDst);
				}
			}
			for (final IChannel c : child.getOutgoing()) {
				final IComponent newSrc = cloneChildren.get(c.getSrc());
				final IComponent newDst;
				if (c.getDst() == null) {
					newDst = null;
				} else {
					newDst = cloneChildren.get(c.getDst());
				}
				c.clone(newSrc, newDst);
			}
		}
		return clone;
	}
}
