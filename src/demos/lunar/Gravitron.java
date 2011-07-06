package demos.lunar;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.util.*;

/**
* Macro-scale celestrial body, simplistic uni-directional
* gravitational model.
**/
public class Gravitron extends PlaneActor implements Collidable {

	/**
	*
	**/
	public final Vector2 force;

	/**
	*
	**/
	public Gravitron(Plane model, Vector2 force) {
		super(model);
		this.force = force;
	}

	/**
	* When the physics engine progresses, this should really simplify to
	* simply checking all physics actors, not just lander. As of now, the
	* physics engine will allow for the lander to "teleport" through the
	* ground.
	**/
	public Set<Action> actions() {
		Set<Action> gravity = new HashSet<Action>();
		for(Actor actor : Groups.ofType(Lander.class, model.actors)) {
			Lander lander = (Lander)actor;
			if(lander.state == Lander.State.IN_FLIGHT) {
				gravity.add(new ForceAction(lander, force));
			}
		}

		Set<Action> ret = new HashSet<Action>();
		ret.add(new CompositeAction(gravity));
		return ret;
	}

	/**
	*
	**/
	public Action collision(Collidable obstacle) {
		return null;
	}

	/**
	* Should be a plane instead... Also, ground level should either be
	* parameterized or should be zero with everything else adapting
	**/
	public Bound bound() {
		return new AlignedBox(-10000, -5, 10000, -10);
	}
}
