package edu.tum.cs.cadmos.core.expressions;

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.EOperator.DEC;
import static edu.tum.cs.cadmos.core.expressions.EOperator.INC;
import static edu.tum.cs.cadmos.core.expressions.EOperator.NEG;
import static edu.tum.cs.cadmos.core.expressions.EOperator.NOT;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

public class UnaryExpression extends AbstractExpression {

	public static final Set<EOperator> UNARY_OPERATORS = unmodifiableSet(new HashSet<>(
			asList(NOT, NEG, INC, DEC)));

	public UnaryExpression(EOperator operator, IExpression operand) {
		super(operator, operand);
		assertTrue(UNARY_OPERATORS.contains(operator),
				"Expected 'operator' to be unary %s, but was '%s'",
				UNARY_OPERATORS, operator);
	}

}
