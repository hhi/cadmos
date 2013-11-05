package edu.tum.cs.cadmos.core.types;

import static java.math.BigInteger.valueOf;

import java.math.BigInteger;

public class IntegerType extends AbstractNumericType<BigInteger> {

	public IntegerType(BigInteger min, BigInteger max) {
		super(EType.INTEGER, min, max);
	}

	public IntegerType() {
		this(valueOf(Integer.MIN_VALUE), valueOf(Integer.MAX_VALUE));
	}

}
