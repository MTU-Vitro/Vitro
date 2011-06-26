package vitro.plane;


public class Frame {
	
	public final double x;
	public final double y;
	public final double angle;
	
	public Frame(double x, double y, double angle) {
		this.x     = x;
		this.y     = y;
		this.angle = normalize(angle);
	}
	
	public Frame translate(double u, double v) {
		return new Frame(x + u, y + v, angle);
	}
	
	public Frame rotate(double rot) {
		return new Frame(x, y, angle + rot);
	}
	
	public Frame transform(double u, double v, double rot) {
		return new Frame(x + u, y + v, angle + rot);
	}
	
	private static double normalize(double angle) {
		return (angle > 0 ? 0 : 2 * Math.PI) + (angle % (2 * Math.PI));
	}
}
