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
 * This is an abstract base class to be used by implementors of the
 * {@link IElement} interface.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating GREEN Hash: 7DE079202965390C7181C83A92866849
 */
public abstract class AbstractElement extends AbstractIdentifiable implements
		IElement {

	/** The display name. */
	private final String name;

	/**
	 * Creates a new abstract element with the given <i>id</i> and <i>name</i>.
	 * <p>
	 * If <code>name == null</code> the name is set to the given <i>id</i>.
	 */
	public AbstractElement(String id, String name) {
		super(id);
		this.name = (name == null) ? getId() : name;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

}
