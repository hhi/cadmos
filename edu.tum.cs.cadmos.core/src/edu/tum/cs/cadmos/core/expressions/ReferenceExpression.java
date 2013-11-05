package edu.tum.cs.cadmos.core.expressions;

public class ReferenceExpression extends AbstractTerminalExpression<String> {

	public ReferenceExpression(String value) {
		super(EOperator.REFERENCE, value);
	}

}
