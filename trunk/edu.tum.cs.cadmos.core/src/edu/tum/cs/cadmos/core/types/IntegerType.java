package edu.tum.cs.cadmos.core.types;

import static java.math.BigInteger.valueOf;

import java.math.BigInteger;

public class IntegerType extends AbstractType {

	private final BigInteger min;

	private final BigInteger max;

	public IntegerType(BigInteger min, BigInteger max) {
		super(EType.INTEGER);
		this.min = min;
		this.max = max;
	}

	public IntegerType() {
		this(valueOf(Integer.MIN_VALUE), valueOf(Integer.MAX_VALUE));
	}

	public BigInteger getMin() {
		return min;
	}

	public BigInteger getMax() {
		return max;
	}

}
