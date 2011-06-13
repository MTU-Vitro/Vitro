package vitro;

import java.awt.Point;

public class Tweener {

	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final double length;
	private final Tweener next;

	private double sofar = 0;

	public Tweener(int x1, double length, Tweener next) {
		this(x1, 0, length, next);
	}

	public Tweener(Point start, double length, Tweener next) {
		this(start.x, start.y, length, next);
	}

	public Tweener(int x1, int y1, double length, Tweener next) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = next.x1;
		this.y2 = next.y1;
		this.length = length;
		this.next = next;
	}

	public Tweener(int x1, int x2, double length) {
		this(x1, 0, x2, 0, length);
	}

	public Tweener(Point start, Point end, double length) {
		this(start.x, start.y, end.x, end.y, length);
	}

	public Tweener(int x1, int y1, int x2, int y2, double length) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.length = length;
		next = null;
	}

	public void tick(double time) {
		if (sofar < length)    { sofar += time; }
		else if (next != null) { next.tick(time); }
	}

	public boolean done() {
		if (sofar < length) { return false; }
		if (next != null)   { return next.done(); }
		return true;
	}

	public int x() {
		return (int)Math.round(xd());
	}

	public int y() {
		return (int)Math.round(yd());
	}

	public double xd() {
		return (sofar <= length) ? tween(x1, x2, sofar/length) :
			      (next != null) ? next.xd() : x2;
	}

	public double yd() {
		return (sofar <= length) ? tween(y1, y2, sofar/length) :
			      (next != null) ? next.yd() : y2;
	}

	public Point position() {
		return new Point(x(), y());
	}

	public void reset() {
		sofar = 0;
		if (next != null) { next.reset(); }
	}

	// by default, a sigmoid eased tween
	protected double tween(double a, double b, double t) {
		double sig = 1/(1 + Math.pow(Math.E, -12*(t-.5)));
		return (a * (1-sig)) + (b * sig);
	}
}