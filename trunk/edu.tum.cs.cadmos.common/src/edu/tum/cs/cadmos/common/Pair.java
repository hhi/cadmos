package edu.tum.cs.cadmos.common;

public class Pair<A, B> {

	private final A a;
	private final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullsafeHashcode(a)
				+ ObjectUtils.nullsafeHashcode(b);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final Pair<A, B> other = (Pair<A, B>) obj;
		return ObjectUtils.nullsafeEquals(a, other.a)
				&& ObjectUtils.nullsafeEquals(b, other.b);
	}
}
