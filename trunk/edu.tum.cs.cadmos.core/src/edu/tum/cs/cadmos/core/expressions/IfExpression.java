package edu.tum.cs.cadmos.core.expressions;


public class IfExpression extends AbstractExpression {

	public IfExpression(IExpression condition, IExpression thenCase,
			IExpression elseCase) {
		super(EOperator.IF, condition, thenCase, elseCase);
	}

}
