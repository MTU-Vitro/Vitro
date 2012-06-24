package vitro.plane;

import vitro.*;

public interface Collidable {
	public Bound   bound();

	public Action  collisionAction(Collidable obstacle);
	public Vector2 collisionVector(Collidable obstacle, Vector2 remaining);
}
