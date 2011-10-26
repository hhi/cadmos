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

package edu.tum.cs.cadmos.matlab.core.translate;

import java.util.HashMap;
import java.util.Map;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.core.model.CompositeComponent;
import edu.tum.cs.cadmos.core.model.IComponent;
import edu.tum.cs.cadmos.core.model.ICompositeComponent;
import edu.tum.cs.simulink.model.SimulinkBlock;

/**
 * A MatlabSimulinkTranslation.
 * 
 * @author
 * @version $Rev: $
 * @version $Author: $
 * @version $Date: $
 * @ConQAT.Rating RED Hash:
 */
public class MatlabSimulinkTranslation {

	public static Map<SimulinkBlock, IComponent> buildGenericMap(SimulinkBlock b) {

		final Map<SimulinkBlock, IComponent> map = new HashMap<>();

		if (b.hasSubBlocks()) {
			final IComponent candidate = map.get(b.getParent());
			Assert.assertTrue(
					candidate instanceof ICompositeComponent,
					"The SimulinkBlock's parent was supposed to be a CompositeComponent but was '%s'",
					candidate);
			map.put(b, new CompositeComponent(b.getId(), b.getName(),
					(ICompositeComponent) candidate));
		}

		return null;

	}

}
