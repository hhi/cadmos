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

import edu.tum.cs.cadmos.commons.core.IListSet;

/**
 * A composite component has a behavior, which is defined by the combined
 * behavior of all of its children components. In particular, a composite
 * component does not have any machine specifications or variables like an
 * {@link IAtomicComponent}.
 * <p>
 * The {@link ICompositeComponent} is the reference implementation of this
 * interface.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public interface ICompositeComponent extends IComponent {

	/** Returns the children components. */
	IListSet<IComponent> getChildren();

}
