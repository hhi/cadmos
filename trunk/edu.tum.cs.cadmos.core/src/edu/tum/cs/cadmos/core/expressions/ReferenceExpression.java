package edu.tum.cs.cadmos.core.expressions;

public class ReferenceExpression extends AbstractExpression {

	private final String id;

	public ReferenceExpression(String id) {
		super(EOperator.REFERENCE);
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
