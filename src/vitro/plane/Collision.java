package vitro.plane;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Collision {
	public static Vector2 earliestCollision(Plane model, Set<Collidable> objects, Collidable actor, Vector2 moveVector) {
		double intersect = 1.0;
		
		AlignedBox actorBound = actor.bound();
		for(Collidable object : objects) {
			if(object != actor) {
				AlignedBox objectBound = object.bound();
				intersect = Math.min(intersect, collide(actorBound, objectBound, moveVector));
				if(intersect < 1.0) { model.collides(actor, object); } // not correct, need to check for FIRST intersection
				break;
			}
		}
		
		return moveVector.mul(intersect);
	}
	
	private static double collide(AlignedBox box0, AlignedBox box1, Vector2 move) {
		double tFirst = 0.0;
		double tLast  = 1.0;

		
		if(move.x < 0.0) {
			if(box0.point1.x < box1.point0.x) return 1.0;
			if(box1.point1.x < box0.point0.x) tFirst = Math.max((box1.point1.x - box0.point0.x) / move.x, tFirst);
			if(box0.point1.x > box1.point0.x) tLast  = Math.min((box1.point0.x - box1.point1.x) / move.x, tLast);
		}
		if(move.x > 0.0) {
			if(box0.point0.x > box1.point1.x) return 1.0;
			if(box0.point1.x < box1.point0.x) tFirst = Math.max((box1.point0.x - box0.point1.x) / move.x, tFirst);
			if(box1.point1.x > box0.point0.x) tLast  = Math.min((box1.point1.x - box0.point0.x) / move.x, tLast);
		}
		
		if(move.y < 0.0) {
			if(box0.point1.y < box1.point0.y) return 1.0;
			if(box1.point1.y < box0.point0.y) tFirst = Math.max((box1.point1.y - box0.point0.y) / move.y, tFirst);
			if(box0.point1.y > box1.point0.y) tLast  = Math.min((box1.point0.y - box1.point1.y) / move.y, tLast);
		}
		
		//System.out.format("%f  %f  %f  %f  %f%n", box0.point0.y, box0.point1.y, box1.point0.y, box1.point1.y, move.y);
		if(move.y > 0.0) {
			if(box0.point0.y > box1.point1.y) return 1.0;
			if(box0.point1.y < box1.point0.y) tFirst = Math.max((box1.point0.y - box0.point1.y) / move.y, tFirst);
			if(box1.point1.y > box0.point0.y) tLast  = Math.min((box1.point1.y - box0.point0.y) / move.y, tLast);
		}
		//System.out.format("%f  %f%n", tFirst, tLast);

		if(tFirst > tLast) { return 1.0; }
		if(tFirst > 0.0)   { return tFirst; }
		return tLast;
	}
}
