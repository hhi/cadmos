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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import edu.tum.cs.cadmos.core.expressions.IExpression;
import edu.tum.cs.cadmos.core.types.IType;

/**
 * A variable has a name, a type and an initial message.
 * <p>
 * This is the reference implementation of the {@link IVariable} interface.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class Variable extends AbstractTypedElement implements IVariable {

	private final IAtomicComponent scope;

	private final IExpression initialMessage;

	public Variable(String id, String name, IType type, IAtomicComponent scope,
			IExpression initialMessage) {
		super(id, name, type);
		assertNotNull(scope, "scope");
		assertNotNull(initialMessage, "initialMessage");
		this.scope = scope;
		this.initialMessage = initialMessage;
		scope.getVariables().add(this);
	}

	public Variable(String id, IType type, IAtomicComponent scope) {
		this(id, null, type, scope, EMPTY_MESSAGE);
	}

	public Variable(String id, IAtomicComponent scope) {
		this(id, null, VOID, scope, EMPTY_MESSAGE);
	}

	/** {@inheritDoc} */
	@Override
	public IAtomicComponent getScope() {
		return scope;
	}

	/** {@inheritDoc} */
	@Override
	public IExpression getInitialMessage() {
		return initialMessage;
	}

	/** {@inheritDoc} */
	@Override
	public IVariable clone(IAtomicComponent newScope) {
		return new Variable(getId(), getName(), getType(), newScope,
				initialMessage);
	}

}
