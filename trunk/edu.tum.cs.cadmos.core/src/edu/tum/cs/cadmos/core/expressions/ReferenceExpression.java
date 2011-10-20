package edu.tum.cs.cadmos.core.expressions;

public class ReferenceExpression extends AbstractExpression {

	private final Object id;

	public ReferenceExpression(Object id) {
		super(EOperator.REFERENCE);
		this.id = id;
	}

	public Object getId() {
		return id;
	}

}
