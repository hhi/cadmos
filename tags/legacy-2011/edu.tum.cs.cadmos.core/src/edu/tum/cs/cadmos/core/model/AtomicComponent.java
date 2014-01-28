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
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	/** The variables in scope. */
	private final IListSet<IVariable> variables = new ListSet<>(this);

	/** The machine, which defines the behavior. */
	private final IMachine machine;

	/**
	 * Creates a new AtomicComponent with the given <i>id</i>, <i>name</i>,
	 * <i>parent</i> and <i>machine</i>.
	 * 
	 * @see AtomicComponent#AtomicComponent(String, ICompositeComponent)
	 */
	public AtomicComponent(String id, String name, ICompositeComponent parent,
			IMachine machine) {
		super(id, name, parent);
		this.machine = machine;
	}

	/**
	 * Creates a new AtomicComponent with the given <i>id</i> and <i>parent</i>,
	 * while the <i>name</i> is set equal to the <i>id</i> and the
	 * <i>machine</i> is set to <code>null</code>.
	 * <p>
	 * This constructor is mainly intended for testing purposes.
	 * 
	 * @see AtomicComponent#AtomicComponent(String, String, ICompositeComponent,
	 *      IMachine)
	 */
	public AtomicComponent(String id, ICompositeComponent parent) {
		this(id, null, parent, null);
	}

	/** {@inheritDoc} */
	@Override
	public IListSet<IVariable> getVariables() {
		return variables;
	}

	/** {@inheritDoc} */
	@Override
	public IMachine getMachine() {
		return machine;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final IAtomicComponent clone = new AtomicComponent(getId(), getName(),
				newParent, getMachine());
		clonePorts(clone);
		for (final IVariable variable : variables) {
			variable.clone(clone);
		}
		return clone;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The assertions of this method ensure that an atomic component cannot have
	 * a variable with the same id as any of the ports.
	 */
	@Override
	public void verifyConsistentAdd(IListCollection<?, ?> collection,
			IIdentifiable element) throws AssertionError {
		if (collection == inbound || collection == outbound) {
			assertNotContainedIn(element, variables, "port element",
					"variables");
		} else if (collection == variables) {
			assertNotContainedIn(element, inbound, "variable element",
					"inbound");
			assertNotContainedIn(element, outbound, "variable element",
					"outbound");
		}
	}

}