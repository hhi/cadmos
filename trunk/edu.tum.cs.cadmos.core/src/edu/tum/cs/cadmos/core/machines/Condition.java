package edu.tum.cs.cadmos.core.machines;

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import edu.tum.cs.cadmos.commons.core.AbstractIdentifiable;
import edu.tum.cs.cadmos.core.expressions.IExpression;

public class Condition extends AbstractIdentifiable implements ICondition {

	private final IExpression expression;

	public Condition(String id, IExpression expression) {
		super(id);
		assertNotNull(expression, "expression");
		this.expression = expression;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

}
