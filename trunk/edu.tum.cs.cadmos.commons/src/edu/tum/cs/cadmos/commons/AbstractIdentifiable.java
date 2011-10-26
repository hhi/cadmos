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

package edu.tum.cs.cadmos.commons;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;

/**
 * Abstract base class for objects that are identified by a unique <i>id</i>.
 * 
 * @see IIdentifiable
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: AE5E679CB3EE4F4B494AC916D94B369A
 */
public abstract class AbstractIdentifiable implements IIdentifiable {

	/** The unique id. */
	private final String id;

	public AbstractIdentifiable(String id) {
		assertNotNull(id, "id");
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return id;
	}

	/** Equality of two {@link IIdentifiable}s is given by their <i>id</i>s. */
	@Override
	public boolean equals(Object other) {
		return (other instanceof IIdentifiable)
				&& ((IIdentifiable) other).getId().equals(getId());
	}

	/** Returns the hashcode of the <i>id</i>. */
	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + getId() + ")";
	}

}
