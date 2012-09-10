package edu.tum.cs.cadmos.analysis;

public class Vector2D {

	public float x;
	public float y;

	public Vector2D(float x, float y) {
		set(x, y);
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float area() {
		return x * y;
	}

	public float norm() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public void translate(float deltaX, float deltaY) {
		x += deltaX;
		y += deltaY;
	}

	public Vector2D delta(Vector2D other) {
		return new Vector2D(x - other.x, y - other.y);
	}

}
