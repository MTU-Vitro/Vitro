package vitro;

import java.awt.Point;

/**
* A utility class for producing 2D eased
* animation "tweens" or interpolation.
* This default implementation provides a
* sigmoid (ease-in, ease-out) response curve.
*
* @author John Earnest
**/
public class Tweener {

	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final double length;
	private final Tweener next;

	private double sofar = 0;

	/**
	* Produce a chained 1D tween.
	*
	* @param x1 the starting x-coordinate of the tween.
	* @param length how long this tween will take (arbitrary units).
	* @param next the Tweener to transition to after this Tweener completes.
	**/
	public Tweener(int x1, double length, Tweener next) {
		this(x1, 0, length, next);
	}

	/**
	* Produce a chained 2D tween.
	*
	* @param start the starting position of the tween.
	* @param length how long this tween will take (arbitrary units).
	* @param next the Tweener to transition to after this Tweener completes.
	**/
	public Tweener(Point start, double length, Tweener next) {
		this(start.x, start.y, length, next);
	}

	/**
	* Produce a chained 2D tween.
	*
	* @param x1 the starting x-coordinate of the tween.
	* @param y1 the starting y-coordinate of the tween.
	* @param length how long this tween will take (arbitrary units).
	* @param next the Tweener to transition to after this Tweener completes.
	**/
	public Tweener(int x1, int y1, double length, Tweener next) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = next.x1;
		this.y2 = next.y1;
		this.length = length;
		this.next = next;
	}

	/**
	* Produce a 1D tween.
	*
	* @param x1 the starting x-coordinate of the tween.
	* @param x2 the final x-coordinate of the tween.
	* @param length how long this tween will take (arbitrary units).
	**/
	public Tweener(int x1, int x2, double length) {
		this(x1, 0, x2, 0, length);
	}

	/**
	* Produce a 2D tween.
	*
	* @param start the starting position of the tween.
	* @param end the final position of the tween.
	* @param length how long this tween will take (arbitrary units).
	**/
	public Tweener(Point start, Point end, double length) {
		this(start.x, start.y, end.x, end.y, length);
	}

	/**
	* Produce a 1D tween.
	*
	* @param x1 the starting x-coordinate of the tween.
	* @param y1 the starting y-coordinate of the tween.
	* @param x2 the final x-coordinate of the tween.
	* @param y2 the final y-coordinate of the tween.
	* @param length how long this tween will take (arbitrary units).
	**/
	public Tweener(int x1, int y1, int x2, int y2, double length) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.length = length;
		next = null;
	}

	/**
	* Advance the timer for this tween or chain of tweens.
	*
	* @param time the change in time since the previous frame.
	**/
	public void tick(double time) {
		if (sofar < length)    { sofar += time; }
		else if (next != null) { next.tick(time); }
	}

	/**
	* Check the status of this tween of chain of tweens.
	*
	* @return true if all tweens are complete.
	**/
	public boolean done() {
		if (sofar < length) { return false; }
		if (next != null)   { return next.done(); }
		return true;
	}

	/**
	* Obtain the tweened x-coordinate in a 2D tween or
	* the only tweened x-coordinate in a 1D tween, in pixels.
	*
	* @return the tweened x-coordinate.
	**/
	public int x() {
		return (int)Math.round(xd());
	}

	/**
	* Obtain the tweened y-coordinate, in pixels.
	* Always returns 0 in a 1D tween.
	*
	* @return the tweened y-coordinate.
	**/
	public int y() {
		return (int)Math.round(yd());
	}

	/**
	* A more accurate representation than x() for small tweens.
	*
	* @return the tweened x-coordinate.
	**/
	public double xd() {
		return (sofar <= length) ? tween(x1, x2, sofar/length) :
			      (next != null) ? next.xd() : x2;
	}

	/**
	* A more accurate representation than y() for small tweens.
	*
	* @return the tweened y-coordinate.
	**/
	public double yd() {
		return (sofar <= length) ? tween(y1, y2, sofar/length) :
			      (next != null) ? next.yd() : y2;
	}

	/**
	* A Point representing the current tweened position.
	*
	* @return the tweened Point.
	**/
	public Point position() {
		return new Point(x(), y());
	}

	/**
	* Reset this Tweener and any later chained Tweener
	* to the initial start point of the animation.
	**/
	public void reset() {
		sofar = 0;
		if (next != null) { next.reset(); }
	}

	/**
	* The actual interpolation function.
	* By default, a sigmoid eased tween.
	*
	* @param a the start point of a 1D tween.
	* @param b the end point of a 1D tween.
	* @param t the tween value, on a range from 0 to 1.
	**/
	protected double tween(double a, double b, double t) {
		double sig = 1/(1 + Math.pow(Math.E, -12*(t-.5)));
		return (a * (1-sig)) + (b * sig);
	}
}