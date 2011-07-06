package demos.lunar;

import vitro.*;
import vitro.plane.*;

/**
*
**/
public class LandingPad extends PlaneActor implements Collidable {

	/**
	*
	**/
	public LandingPad(Plane model, Position position) {
		super(model);
		model.positions.put(this, position);
	}

	// methods for evaluating a successful landing vs an unsuccessful one.
	public boolean correct(Lander lander) {
		AlignedBox bound0 = (AlignedBox)lander.bound();
		AlignedBox bound1 = (AlignedBox)this.bound();

		//System.out.println(bound0.point0.x + " " + bound1.point1.x + " " + bound0.point1.x + " " + bound1.point1.x);

		if(bound0.point0.x > bound1.point0.x && bound0.point1.x < bound1.point1.x) {
			return true;
		}
		return false;
	}

	/**
	*
	**/
	public Action collision(Collidable obstacle) {
		return null;
	}

	/**
	*
	**/
	public Bound bound() {
		Position p = model.positions.get(this);
		return new AlignedBox(p.x - 35, -5, p.x + 35, p.y);
	}
}
