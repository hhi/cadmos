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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotContainedIn;
import edu.tum.cs.cadmos.commons.core.IIdentifiable;
import edu.tum.cs.cadmos.commons.core.IListCollection;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.machines.IMachine;

/**
 * An atomic component has a behavior defined by an {@link IMachine} and some
 * {@link IVariable}s. In particular, an atomic component does not have any
 * children components like an {@link ICompositeComponent}.
 * <p>
 * This is the reference implementation of the {@link IAtomicComponent}
 * interface.
 * <p>
 * External API users should use
 * {@link ModelPackage#createAtomicComponent(String, String, ICompositeComponent, IMachine)}
 * rather than directly creating an instance.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
/* package */class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	private final IListSet<IVariable> variables = new ListSet<>(this);

	private final IMachine machine;

	public AtomicComponent(String id, String name, ICompositeComponent parent,
			IMachine machine) {
		super(id, name, parent);
		this.machine = machine;
	}

	public AtomicComponent(String id, ICompositeComponent parent) {
		this(id, null, parent, null);
	}

	@Override
	public IListSet<IVariable> getVariables() {
		return variables;
	}

	@Override
	public IMachine getMachine() {
		return machine;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final IAtomicComponent clone = new AtomicComponent(getId(), getName(),
				newParent, getMachine());
		for (final IVariable variable : variables) {
			variable.clone(clone);
		}
		return clone;
	}

	/** {@inheritDoc} */
	@Override
	public void verifyConsistentAdd(IListCollection<?, ?> collection,
			IIdentifiable element) throws AssertionError {
		if (collection == incoming || collection == outgoing) {
			assertNotContainedIn(element, variables, "channel", "variables");
		} else if (collection == variables) {
			assertNotContainedIn(element, incoming, "variable", "incoming");
			assertNotContainedIn(element, outgoing, "variable", "outgoing");
		}
	}

}
