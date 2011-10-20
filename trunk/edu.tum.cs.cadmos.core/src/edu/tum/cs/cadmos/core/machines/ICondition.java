package edu.tum.cs.cadmos.core.machines;

import edu.tum.cs.cadmos.core.expressions.IExpression;

public interface ICondition {

	Object getId();

	IExpression getExpression();

}
