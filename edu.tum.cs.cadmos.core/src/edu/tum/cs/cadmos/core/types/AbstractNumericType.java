package edu.tum.cs.cadmos.core.types;


public abstract class AbstractNumericType<T extends Number> extends
		AbstractType {

	private final T min;

	private final T max;

	public AbstractNumericType(EType type, T min, T max) {
		super(type);
		this.min = min;
		this.max = max;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

}
