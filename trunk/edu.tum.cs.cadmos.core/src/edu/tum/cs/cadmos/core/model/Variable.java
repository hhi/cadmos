package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.ConstantExpression.EMPTY_MESSAGE;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import edu.tum.cs.cadmos.core.expressions.IExpression;
import edu.tum.cs.cadmos.core.types.IType;

public class Variable extends AbstractTypedElement implements IVariable {

	private final IAtomicComponent scope;

	private final IExpression initialMessage;

	public Variable(String id, String name, IType type, IAtomicComponent scope,
			IExpression initialMessage) {
		super(id, name, type);
		assertNotNull(scope, "scope");
		assertTrue(!scope.getVariables().contains(getId()),
				"Variable with id '%s' is present in '%s' already", id, scope);
		assertTrue(
				!scope.getIncoming().contains(getId()),
				"Channel with id '%s' is present in incoming channels of '%s' already",
				id, scope);
		assertTrue(
				!scope.getOutgoing().contains(getId()),
				"Channel with id '%s' is present in outgoing channels of '%s' already",
				id, scope);
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
