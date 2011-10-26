package edu.tum.cs.cadmos.core.machines;

import edu.tum.cs.cadmos.commons.core.IIdentifiable;
import edu.tum.cs.cadmos.core.expressions.IExpression;

public interface ICondition extends IIdentifiable {

	IExpression getExpression();

}
