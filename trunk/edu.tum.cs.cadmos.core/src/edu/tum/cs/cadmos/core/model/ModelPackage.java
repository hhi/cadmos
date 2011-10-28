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

import java.util.List;

import edu.tum.cs.cadmos.core.expressions.IExpression;
import edu.tum.cs.cadmos.core.machines.IMachine;
import edu.tum.cs.cadmos.core.types.IType;

/**
 * Static factory for elements in the <span
 * style="font-variant:small-caps">Cadmos</span> <code>core.model</code>
 * package.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class ModelPackage {

	public static IChannel createChannel(String id, String name, IType type,
			IComponent src, IComponent dst, List<IExpression> initialMessages,
			int srcRate, int dstRate) {
		return new Channel(id, name, type, src, dst, initialMessages, srcRate,
				dstRate);
	}

	public static IAtomicComponent createAtomicComponent(String id,
			String name, ICompositeComponent parent, IMachine machine) {
		return new AtomicComponent(id, name, parent, machine);
	}

	public static ICompositeComponent createCompositeComponent(String id,
			String name, ICompositeComponent parent) {
		return new CompositeComponent(id, name, parent);
	}

	public static IVariable createVariable(String id, String name, IType type,
			IAtomicComponent scope, IExpression initialMessage) {
		return new Variable(id, name, type, scope, initialMessage);
	}

}