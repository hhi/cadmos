package edu.tum.cs.cadmos.core.types;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

public class RealType extends AbstractNumericType<BigDecimal> {

	public RealType(BigDecimal min, BigDecimal max) {
		super(EType.REAL, min, max);
	}

	public RealType() {
		this(valueOf(-Float.MAX_VALUE), valueOf(Float.MAX_VALUE));
	}

}
