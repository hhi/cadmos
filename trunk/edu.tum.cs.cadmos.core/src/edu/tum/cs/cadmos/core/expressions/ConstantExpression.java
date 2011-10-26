package edu.tum.cs.cadmos.core.expressions;

public class ConstantExpression extends AbstractExpression {

	public static final IExpression EMPTY_MESSAGE = new ConstantExpression("<EMPTY_MESSAGE>");

	private final Object value;

	public ConstantExpression(Object value) {
		super(EOperator.CONSTANT);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

}
