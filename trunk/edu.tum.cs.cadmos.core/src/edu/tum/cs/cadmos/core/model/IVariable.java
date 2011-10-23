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

/**
 * A typed variable with id and name that exists within the scope of an
 * {@link IAtomicComponent}.
 * <p>
 * A clone of this variable, which is added to a new scope, is created with
 * {@link #clone(IAtomicComponent)}.
 * 
 * @author wolfgang.schwitzer
 * @author $Author$
 * @version $Rev$
 * @ConQAT.Rating RED Hash:
 */
public interface IVariable extends ITypedElement {

	/** Returns the component scope in which this variable exists. */
	IAtomicComponent getScope();

	/** Returns a clone of this variable within the given <i>newScope</i>. */
	IVariable clone(IAtomicComponent newScope);

}
