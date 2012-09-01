package edu.tum.cs.cadmos.core.expressions;

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;

public class SelectExpression extends AbstractExpression {

	public SelectExpression(ReferenceExpression... pathNodes) {
		super(EOperator.SELECT, pathNodes);
		assertTrue(pathNodes.length >= 2,
				"Expected >= 2 'pathNodes', but was '%s'", pathNodes.length);
	}
}
