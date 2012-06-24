package demos.lunar;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.util.*;

/**
*
**/
public class Lunar extends Plane {
	public final Gravitron  gravitron;
	public final LandingPad landingPad;

	private final CollectionObserver<Actor> actorObserver = new ActorObserver();
	private final CollectionObserver<Map.Entry<Actor, Position>> positionObserver = new PositionObserver();

	/**
	*
	**/
	public Lunar(double gravity, Position target) {
		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableMap<Actor, Position>)positions).addObserver(positionObserver);

		if(gravity > 0) { gravity *= -1; }
		this.gravitron  = new Gravitron(this, new Vector2(0.0, gravity));
		model.actors.add(gravitron);
		this.landingPad = new LandingPad(this, target);
		model.actors.add(landingPad);
	}


	/**
	*
	**/
	public boolean done() {
		for(Actor actor : actors) {
			if(actor instanceof Lander && ((Lander)actor).state == Lander.State.IN_FLIGHT) {
				return false;
			}
		}
		return true;
	}

	/**
	*
	**/
	private class ActorObserver implements CollectionObserver<Actor> {
		public void added(ObservableCollection sender, Actor e) {

		}

		public void removed(ObservableCollection sender, Actor e) {

		}
	}

	private class PositionObserver implements CollectionObserver<Map.Entry<Actor, Position>> {
		public void added(ObservableCollection sender, Map.Entry<Actor, Position> e) {
			if(e.getKey() instanceof Lander) { actors.add(((Lander)e.getKey()).navigation); }
		}
		
		public void removed(ObservableCollection sender, Map.Entry<Actor, Position> e) {
			if(e.getKey() instanceof Lander) { actors.remove(((Lander)e.getKey()).navigation); }
		}
	}

}
