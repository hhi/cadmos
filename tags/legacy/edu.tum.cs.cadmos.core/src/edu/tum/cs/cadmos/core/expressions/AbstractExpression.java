package edu.tum.cs.cadmos.core.expressions;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public abstract class AbstractExpression implements IExpression {

	private final EOperator operator;

	private final List<IExpression> operands;

	public AbstractExpression(EOperator operator, IExpression... operands) {
		this.operator = operator;
		this.operands = unmodifiableList(asList(operands));
	}

	@Override
	public EOperator getOperator() {
		return operator;
	}

	@Override
	public List<IExpression> getOperands() {
		return operands;
	}

}
