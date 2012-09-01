package edu.tum.cs.cadmos.core.expressions;

import java.util.List;

public interface IExpression {

	EOperator getOperator();

	List<IExpression> getOperands();

}
