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
import edu.tum.cs.cadmos.commons.core.AbstractElement;
import edu.tum.cs.cadmos.core.types.IType;

/**
 * This is the abstract reference implementation of the {@link ITypedElement}
 * interface, which servers as a base class for the {@link Port} and
 * {@link Variable} implementations.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public abstract class AbstractTypedElement extends AbstractElement implements
		ITypedElement {

	/** The data type of this element. */
	private final IType type;

	/**
	 * Creates a new AbstractTypedElement with the given <i>id</i>, <i>name</i>
	 * and <i>type</i>.
	 */
	public AbstractTypedElement(String id, String name, IType type) {
		super(id, name);
		assertNotNull(type, "type");
		this.type = type;
	}

	/** {@inheritDoc} */
	@Override
	public IType getType() {
		return type;
	}

}
