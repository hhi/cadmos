package edu.tum.cs.cadmos.core.expressions;

public enum EOperator {

	/** Parallel combination: {@link ParallelExpression} */
	PARALLEL(";"),

	/** Select along path: {@link SelectExpression} */
	SELECT("."),

	/** Constant value: {@link ConstantExpression} */
	CONSTANT(),

	/** Reference to identifier: {@link ReferenceExpression} */
	REFERENCE(),

	/** Boolean negation: {@link UnaryExpression} */
	NOT("!"),

	/** Arithmetic negation: {@link UnaryExpression} */
	NEG("-"),

	/** Arithmetic increment: {@link UnaryExpression} */
	INC("++"),

	/** Arithmetic decrement: {@link UnaryExpression} */
	DEC("--"),

	ASSIGN("="),

	/** Arithmetic addition: {@link BinaryExpression} */
	ADD("+"),

	/** Arithmetic subtraction: {@link BinaryExpression} */
	SUB("-"),

	/** Arithmetic multiplication: {@link BinaryExpression} */
	MUL("*"),

	/** Arithmetic division: {@link BinaryExpression} */
	DIV("/"),

	/** Arithmetic modulo: {@link BinaryExpression} */
	MOD("%"),

	/** Equality: {@link BinaryExpression} */
	EQ("=="),

	/** Non-equality: {@link BinaryExpression} */
	NEQ("!="),

	/** Greater than: {@link BinaryExpression} */
	GT(">"),

	/** Greater than or equal: {@link BinaryExpression} */
	GTE(">="),

	/** Less than: {@link BinaryExpression} */
	LT("<"),

	/** Less than or equal: {@link BinaryExpression} */
	LTE(">="),

	/** Lazy boolean conjunction: {@link BinaryExpression} */
	AND("&&"),

	/** Lazy boolean disjunction: {@link BinaryExpression} */
	OR("||"),

	/** Boolean exclusive disjunction: {@link BinaryExpression} */
	XOR("^"),

	/** Strict boolean and binary conjunction: {@link BinaryExpression} */
	STRICT_AND("&"),

	/** Strict boolean and binary disjunction: {@link BinaryExpression} */
	STRICT_OR("|"),

	/** Binary complement: {@link BinaryExpression} */
	BITWISE_COMPLEMENT("~"),

	/** Binary shift left: {@link BinaryExpression} */
	SHL("<<"),

	/** Binary shift right with sign extend: {@link BinaryExpression} */
	SHR(">>"),

	/** Binary shift right with zero extend: {@link BinaryExpression} */
	UNSIGNED_SHR(">>>"),

	/** Conditional if-then-else: {@link IfElseExpression} */
	IF_ELSE("if", "else");

	private final String[] symbols;

	private EOperator(String... symbols) {
		this.symbols = symbols;
	}

	public String[] getSymbols() {
		return symbols.clone();
	}

}
