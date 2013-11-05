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
 * Verifies whether an intended add of a new element to an
 * {@link IListCollection} is consistent and throws and {@link AssertionError}
 * otherwise.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public interface IConsistencyVerifier {

	/**
	 * Verifies whether an intended add of the new <i>element</i> to an
	 * {@link IListCollection} will be consistent and throws and
	 * {@link AssertionError} otherwise.
	 */
	void verifyConsistentAdd(IListCollection<?, ?> collection,
			IIdentifiable element) throws AssertionError;

}
