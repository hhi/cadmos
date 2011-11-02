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
 * An element is identified by a unique <i>id</i> and has an optionally
 * non-unique display <i>name</i>.
 * <p>
 * Implementations must follow the convention to return the <i>id</i> as
 * <i>name</i> if no explicit display name has been given by (e.g.
 * <code>name == null</code>).
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: 215E25E4A95BE897EDDCC487720F0D3E
 */
public interface IElement extends IIdentifiable {

	/**
	 * Returns the display name of this element. If no explicit name has been
	 * given to this element, the same value as obtained by {@link #getId()} is
	 * returned.
	 */
	String getName();

}
