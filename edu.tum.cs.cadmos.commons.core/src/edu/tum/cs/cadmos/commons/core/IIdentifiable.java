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

package edu.tum.cs.cadmos.commons.core;

/**
 * An object that is identified by a unique <i>id</i>.
 * <p>
 * IIdentifieable objects have the following conventions:
 * <ul>
 * <li>The <i>id</i> must not be <code>null</code>.
 * <li>Equality is given by <i>id</i>s ({@link #equals(Object)} is implemented
 * accordingly).
 * <li>The hashcode is given by the <i>id</i>'s hashcode ({@link #hashCode()} is
 * implemented accordingly).
 * </ul>
 * 
 * @see AbstractIdentifiable
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: AE806F11DBD60EB7F8D0B98BE5FC8C41
 */
public interface IIdentifiable {

	/** Returns the unique id of this object. */
	String getId();

}
