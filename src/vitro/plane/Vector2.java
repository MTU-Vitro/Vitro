package vitro.plane;

/**
 *
 **/
public class Vector2 {
	/**
	 *
	 **/
	public static final Vector2 ZERO = new Vector2(0.0, 0.0);

	/**
	 *
	 **/
	public final double x;

	/**
	 *
	 **/
	public final double y;

	/**
	 *
	 **/
	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 *
	 **/
	public Vector2 add(Vector2 v) {
		return new Vector2(x + v.x, y + v.y);
	}

	/**
	 *
	 **/
	public Vector2 sub(Vector2 v) {
		return new Vector2(x - v.x, y - v.y);
	}

	/**
	 *
	 **/
	public Vector2 mul(double s) {
		return new Vector2(x * s, y * s);
	}

	/**
	 *
	 **/
	public Vector2 normalize() {
		double norm = this.norm();
		return norm != 0.0 ? this.mul(1.0 / this.norm()) : ZERO;
	}

	/**
	 *
	 **/
	public double dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	/**
	 *
	 **/
	public double norm() {
		return Math.sqrt(this.dot(this));
	}

	/**
	 *
	 **/
	public double normSq() {
		return this.dot(this);
	}

	/**
	 *
	 **/
	public Vector2 reflect(Vector2 normal) {
		return this.sub(normal.mul(2 * this.dot(normal)));
	}

	/**
	 *
	 **/
	public Vector2 refract(Vector2 normal, double index1, double index2) {
		// index ratio
		double ratio = index1 / index2;

		// angle calculations
		double cos1 = - normal.dot(this);
		double cos2 = Math.sqrt(1 - ratio * ratio * (1 - cos1 * cos1));
		if(Double.isNaN(cos2)) {
			return null;
		}

		// snell's law
		int mul = (cos1 >= 0 ? +1 : -1);
		return this.mul(ratio).add(normal.mul(ratio * cos1 - mul * cos2));
	}
	
	/**
	 *
	 **/
	public String toString() {
		return String.format("%f, %f", x, y);
	}
}
