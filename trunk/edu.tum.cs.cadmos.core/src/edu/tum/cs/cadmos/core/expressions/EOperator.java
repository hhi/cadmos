package edu.tum.cs.cadmos.core.expressions;

public enum EOperator {

	/** Parallel combination: {@link ParallelExpression} */
	PARALLEL("_;_"),

	/** Select along path: {@link SelectExpression} */
	SELECT("_._"),

	/** Constant value: {@link ConstantExpression} */
	CONSTANT("_"),

	/** Reference to identifier: {@link ReferenceExpression} */
	REFERENCE("_"),

	/** Boolean negation: {@link UnaryExpression} */
	NOT("!_"),

	/** Arithmetic negation: {@link UnaryExpression} */
	NEG("-_"),

	/** Arithmetic increment: {@link UnaryExpression} */
	INC("++_"),

	/** Arithmetic decrement: {@link UnaryExpression} */
	DEC("--_"),

	ASSIGN("_=_"),

	/** Arithmetic addition: {@link BinaryExpression} */
	ADD("_+_"),

	/** Arithmetic subtraction: {@link BinaryExpression} */
	SUB("_-_"),

	/** Arithmetic multiplication: {@link BinaryExpression} */
	MUL("_*_"),

	/** Arithmetic division: {@link BinaryExpression} */
	DIV("_/_"),

	/** Arithmetic modulo: {@link BinaryExpression} */
	MOD("_%_"),

	/** Equality: {@link BinaryExpression} */
	EQ("_==_"),

	/** Non-equality: {@link BinaryExpression} */
	NEQ("_!=_"),

	/** Greater than: {@link BinaryExpression} */
	GT("_>_"),

	/** Greater than or equal: {@link BinaryExpression} */
	GTE("_>=_"),

	/** Less than: {@link BinaryExpression} */
	LT("_<_"),

	/** Less than or equal: {@link BinaryExpression} */
	LTE("_>=_"),

	/** Lazy boolean conjunction: {@link BinaryExpression} */
	AND("_&&_"),

	/** Lazy boolean disjunction: {@link BinaryExpression} */
	OR("_||_"),

	/** Boolean exclusive disjunction: {@link BinaryExpression} */
	XOR("_^_"),

	/** Strict boolean and binary conjunction: {@link BinaryExpression} */
	STRICT_AND("_&_"),

	/** Strict boolean and binary disjunction: {@link BinaryExpression} */
	STRICT_OR("_|_"),

	/** Binary complement: {@link BinaryExpression} */
	BITWISE_COMPLEMENT("~_"),

	/** Binary shift left: {@link BinaryExpression} */
	SHL("_<<_"),

	/** Binary shift right with sign extend: {@link BinaryExpression} */
	SHR("_>>_"),

	/** Binary shift right with zero extend: {@link BinaryExpression} */
	UNSIGNED_SHR("_>>>_"),

	/** Conditional if-then-else: {@link IfExpression} */
	IF("if (_) _ else _");

	private final String pattern;

	private EOperator(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

}
