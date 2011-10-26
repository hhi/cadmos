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

/**
 * A channel is a typed element that connects a source and a destination
 * component.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: 918F24F813D187B494D5ED1A3F260CC4
 */
public interface IChannel extends ITypedElement {

	IComponent getSrc();

	IComponent getDst();

	int getDelay();

	List<IExpression> getInitialMessages();

	int getSrcRate();

	int getDstRate();

	/**
	 * Returns a clone of this channel that connects from the given
	 * <i>newSrc</i> to the given <i>newDst</i>.
	 */
	IChannel clone(IComponent newSrc, IComponent newDst);

}
