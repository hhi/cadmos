package edu.tum.cs.cadmos.core.expressions;

public class ConstantExpression extends AbstractTerminalExpression<Object> {

	public static final IExpression EMPTY_MESSAGE = new ConstantExpression(
			"<EMPTY_MESSAGE>");

	public ConstantExpression(Object value) {
		super(EOperator.CONSTANT, value);
	}

}
