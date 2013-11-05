package edu.tum.cs.cadmos.core.expressions;


public class IfElseExpression extends AbstractExpression {

	public IfElseExpression(IExpression condition, IExpression thenCase,
			IExpression elseCase) {
		super(EOperator.IF_ELSE, condition, thenCase, elseCase);
	}

}
