package vitro.plane;

import vitro.*;

public interface Collidable {
	public Bound   bound();
	public Vector2 collisionVector(Collidable obstacle, Vector2 remaining);
	public Action  collisionAction(Collidable obstacle);
}
