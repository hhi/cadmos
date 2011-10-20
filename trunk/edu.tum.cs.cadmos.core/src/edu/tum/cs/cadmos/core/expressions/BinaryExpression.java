package edu.tum.cs.cadmos.core.expressions;

import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.EOperator.ADD;
import static edu.tum.cs.cadmos.core.expressions.EOperator.AND;
import static edu.tum.cs.cadmos.core.expressions.EOperator.ASSIGN;
import static edu.tum.cs.cadmos.core.expressions.EOperator.BITWISE_COMPLEMENT;
import static edu.tum.cs.cadmos.core.expressions.EOperator.DIV;
import static edu.tum.cs.cadmos.core.expressions.EOperator.EQ;
import static edu.tum.cs.cadmos.core.expressions.EOperator.GT;
import static edu.tum.cs.cadmos.core.expressions.EOperator.GTE;
import static edu.tum.cs.cadmos.core.expressions.EOperator.LT;
import static edu.tum.cs.cadmos.core.expressions.EOperator.LTE;
import static edu.tum.cs.cadmos.core.expressions.EOperator.MOD;
import static edu.tum.cs.cadmos.core.expressions.EOperator.MUL;
import static edu.tum.cs.cadmos.core.expressions.EOperator.NEQ;
import static edu.tum.cs.cadmos.core.expressions.EOperator.OR;
import static edu.tum.cs.cadmos.core.expressions.EOperator.SHL;
import static edu.tum.cs.cadmos.core.expressions.EOperator.SHR;
import static edu.tum.cs.cadmos.core.expressions.EOperator.STRICT_AND;
import static edu.tum.cs.cadmos.core.expressions.EOperator.STRICT_OR;
import static edu.tum.cs.cadmos.core.expressions.EOperator.SUB;
import static edu.tum.cs.cadmos.core.expressions.EOperator.UNSIGNED_SHR;
import static edu.tum.cs.cadmos.core.expressions.EOperator.XOR;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

public class BinaryExpression extends AbstractExpression {

	public static final Set<EOperator> BINARY_OPERATORS = unmodifiableSet(new HashSet<>(
			asList(ASSIGN, ADD, SUB, MUL, DIV, MOD, EQ, NEQ, GT, GTE, LT, LTE,
					AND, OR, XOR, STRICT_AND, STRICT_OR, BITWISE_COMPLEMENT,
					SHL, SHR, UNSIGNED_SHR)));

	public BinaryExpression(EOperator operator, IExpression leftOperand,
			IExpression rightOperand) {
		super(operator, leftOperand, rightOperand);
		assertTrue(BINARY_OPERATORS.contains(operator),
				"Expected 'operator' to be binary %s, but was '%s'",
				BINARY_OPERATORS, operator);
	}

}
