package vitro.plane;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Collision {
	public final Collidable intercepted;
	public final Vector2    intercept;

	public Collision(Collidable intercepted, Vector2 intercept) {
		this.intercepted = intercepted;
		this.intercept   = intercept;
	}

	/**
	* Can we handle simultaneous collisions?
	**/
	public static Collision collision(Plane model, Collidable moving, Vector2 move) {
		Collidable intercepted = null;
		double intercept = 1.0;

		for(Actor a : model.actors) {
			if(a instanceof Collidable && moving != a) {
				Collidable obstacle = (Collidable)a;

				Bound bound0 = moving.bound();
				Bound bound1 = obstacle.bound();

				double param;
				if(bound0 instanceof AlignedBox && bound1 instanceof AlignedBox) {
					param = collision((AlignedBox)bound0, (AlignedBox)bound1, move);
				}
				else if(bound0 instanceof Circle && bound1 instanceof Circle) {
					param = collision((Circle)bound0, (Circle)bound1, move);
				}
				else {
					throw new Error("Collision not supported!");
				}

				if(-0.01 <= param && param <= intercept) {
					intercepted = obstacle;
					intercept = param;
				}
			}
		}

		return new Collision(intercepted, move.mul(intercept));
	}

	private static double collision(Circle circle0, Circle circle1, Vector2 move) {
		Vector2 diff = (circle0.center).displace(circle1.center);
		double  rads = (circle0.radius + circle1.radius) * (circle0.radius + circle1.radius);
		
		if(move.dot(diff) <= 0) { return Double.POSITIVE_INFINITY; }

		double a =      move.dot(move);
		double b = -2 * move.dot(diff);
		double c =      diff.dot(diff) - rads;

		double discrim = b * b - 4 * a * c;
		if(discrim >= 0) {
			double sqrt = Math.sqrt(discrim);

			double t0 = (-b - sqrt) / (2 * a);
			double t1 = (-b + sqrt) / (2 * a);

			if(t0 >= -0.01) { return t0; }
			if(t1 >= -0.01) { return t1; }
		}
		return Double.POSITIVE_INFINITY;
	}

	private static double collision(AlignedBox box0, AlignedBox box1, Vector2 move) {
		double tFirst = Double.NEGATIVE_INFINITY;
		double tLast  = Double.POSITIVE_INFINITY;  // what is tLast good for?

		if(move.x < 0.0) {
			if(box0.point1.x <= box1.point0.x) return Double.POSITIVE_INFINITY;
			if(box1.point1.x <= box0.point0.x) tFirst = Math.max((box1.point1.x - box0.point0.x) / move.x, tFirst);
			if(box0.point1.x >= box1.point0.x) tLast  = Math.min((box1.point0.x - box1.point1.x) / move.x, tLast);
		}
		if(move.x > 0.0) {
			if(box0.point0.x >= box1.point1.x) return Double.POSITIVE_INFINITY;
			if(box0.point1.x <= box1.point0.x) tFirst = Math.max((box1.point0.x - box0.point1.x) / move.x, tFirst);
			if(box1.point1.x >= box0.point0.x) tLast  = Math.min((box1.point1.x - box0.point0.x) / move.x, tLast);
		}

		if(move.y < 0.0) {
			if(box0.point1.y <= box1.point0.y) return Double.POSITIVE_INFINITY;
			if(box1.point1.y <= box0.point0.y) tFirst = Math.max((box1.point1.y - box0.point0.y) / move.y, tFirst);
			if(box0.point1.y >= box1.point0.y) tLast  = Math.min((box1.point0.y - box1.point1.y) / move.y, tLast);
		}
		if(move.y > 0.0) {
			if(box0.point0.y >= box1.point1.y) return Double.POSITIVE_INFINITY;
			if(box0.point1.y <= box1.point0.y) tFirst = Math.max((box1.point0.y - box0.point1.y) / move.y, tFirst);
			if(box1.point1.y >= box0.point0.y) tLast  = Math.min((box1.point1.y - box0.point0.y) / move.y, tLast);
		}

		if(tFirst >= 0.0)   { return tFirst; }
		return Double.POSITIVE_INFINITY;
	}
}
