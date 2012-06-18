package vitro.plane;

public class Circle extends Bound {
	public final Position center;
	public final double   radius;
	
	public Circle(double x, double y, double radius) {
		this.center = new Position(x, y);
		this.radius = radius;
	}
}
