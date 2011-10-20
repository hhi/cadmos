package edu.tum.cs.cadmos.core.expressions;

public class ConstantExpression extends AbstractExpression {

	private final Object value;

	public ConstantExpression(Object value) {
		super(EOperator.CONSTANT);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

}
