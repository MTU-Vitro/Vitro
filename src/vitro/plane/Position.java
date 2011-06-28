package vitro.plane;


public class Position {
	public static final Position ZERO = new Position(0.0, 0.0);

	public final double x;
	public final double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Position(Vector2 v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Position translate(double u, double v) {
		return new Position(x + u, y + v);
	}

	public Position translate(Vector2 v) {
		return new Position(x + v.x, y + v.y);
	}

	public Vector2 displace(Position p) {
		return new Vector2(p.x - x, p.y - y);
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Position)) { return false; }
		Position other = (Position)o;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}
}
