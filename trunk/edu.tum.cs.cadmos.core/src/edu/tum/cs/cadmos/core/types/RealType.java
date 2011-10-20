package edu.tum.cs.cadmos.core.types;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;

public class RealType extends AbstractType {

	private final BigDecimal min;

	private final BigDecimal max;

	public RealType(BigDecimal min, BigDecimal max) {
		super(EType.REAL);
		this.min = min;
		this.max = max;
	}

	public RealType() {
		this(valueOf(-Float.MAX_VALUE), valueOf(Float.MAX_VALUE));
	}

	public BigDecimal getMin() {
		return min;
	}

	public BigDecimal getMax() {
		return max;
	}

}
