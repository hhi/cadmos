package edu.tum.cs.cadmos.core.machines;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import edu.tum.cs.cadmos.core.expressions.IExpression;

public class Condition implements ICondition {

	private final String id;

	private final IExpression expression;

	public Condition(String id, IExpression expression) {
		assertNotNull(id, "id");
		assertNotNull(expression, "expression");
		this.id = id;
		this.expression = expression;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

}
