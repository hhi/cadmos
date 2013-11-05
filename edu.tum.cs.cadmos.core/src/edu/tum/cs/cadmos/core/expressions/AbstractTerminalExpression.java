package edu.tum.cs.cadmos.core.expressions;

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.expressions.EOperator.CONSTANT;
import static edu.tum.cs.cadmos.core.expressions.EOperator.REFERENCE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTerminalExpression<T> extends AbstractExpression {

	public static final Set<EOperator> TERMINAL_OPERATORS = unmodifiableSet(new HashSet<>(
			asList(CONSTANT, REFERENCE)));

	private final T value;

	public AbstractTerminalExpression(EOperator operator, T value) {
		super(operator);
		assertTrue(TERMINAL_OPERATORS.contains(operator),
				"Expected 'operator' to be terminal %s, but was '%s'",
				TERMINAL_OPERATORS, operator);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

}
