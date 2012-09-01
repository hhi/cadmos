package edu.tum.cs.cadmos.core.expressions;

public class ParallelExpression extends AbstractExpression {

	public ParallelExpression(IExpression... operands) {
		super(EOperator.PARALLEL, operands);
	}

}
