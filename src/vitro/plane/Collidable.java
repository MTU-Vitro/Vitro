package vitro.plane;

import vitro.*;

public interface Collidable {
	public Action collision(Collidable obstacle);
	public Bound  bound();
}
