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
			if(a instanceof Collidable) {
				Collidable obstacle = (Collidable)a;

				Bound bound0 = moving.bound();
				Bound bound1 = obstacle.bound();

				if(!(bound0 instanceof AlignedBox) && !(bound1 instanceof AlignedBox)) {
					throw new Error("Collision not supported!");
				}

				double param = collision((AlignedBox)bound0, (AlignedBox)bound1, move);
				if(0.0 <= param && param <= intercept) {
					intercepted = obstacle;
					intercept = param;
				}
			}
		}

		return new Collision(intercepted, move.mul(intercept));
	}


	private static double collision(AlignedBox box0, AlignedBox box1, Vector2 move) {
		boolean collision = false;

		double tFirst = Double.NEGATIVE_INFINITY;
		double tLast  = Double.POSITIVE_INFINITY;

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

		//if(tFirst > tLast) { return 1.0; }
		if(tFirst >= 0.0)   { return tFirst; }
		//return tLast;
		return Double.POSITIVE_INFINITY;
	}
}
