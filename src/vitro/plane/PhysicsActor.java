package vitro.plane;

import vitro.*;
import vitro.plane.*;
import java.util.*;

/**
* More realistic would be to keep tabs on momentum, so that
* changes in mass will be appropriately expressed. That change
* shouldn't be a large refactor though, so I'm keeping this
* simplistic approach for now.
**/
public class PhysicsActor extends PlaneActor {
	public final double mass;  // should this be final?
	public Vector2 velocity;

	public PhysicsActor(Plane model, double mass, Vector2 initial) {
		super(model);
		this.mass     = mass;
		this.velocity = initial;
	}

	public PhysicsActor(Plane model, double mass) {
		this(model, mass, Vector2.ZERO);
	}

	public Set<Action> actions() {
		Set<Action> ret = new HashSet<Action>();
		ret.add(new VelocityAction(model, this));
		return ret;
	}
}
