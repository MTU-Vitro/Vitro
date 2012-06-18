package demos.lights;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import static vitro.util.Groups.*;

public class LightsOut extends Grid {

	public LightsOut(int width, int height) {
		super(width, height);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				locations.put(new Light(this), new Location(this, x, y));
			}
		}
		actors.add(new Player());
	}

	public void shuffle() {
		for(Actor a : actors) {
			if (a instanceof Light && Math.random() > .5) {
				((Light)a).toggle();
				for(Location other : ((Light)a).neighbors(ORTHOGONAL)) {
					((Light)model.actorAt(other)).toggle();
				}
			}
		}
	}

	public boolean done() {
		for(Actor a : actors) {
			if(a instanceof Light && ((Light)a).on()) { return false; }
		}
		return true;
	}

	public class Light extends GridActor implements Factional {
		private int team;

		protected Light(Grid model) {
			super(model);
		}

		public int team() {
			return team;
		}

		protected boolean on() {
			return team == 1;
		}

		protected void toggle() {
			team = team == 0 ? 1 : 0;
		}
	}

	public class Player extends Actor {
		public boolean[][] state() {
			boolean[][] state = new boolean[height][width];
			for(Actor actor : locations.keySet()) {
				if(actor instanceof Light) {
					Location location = locations.get(actor);
					state[location.y][location.x] = ((Light)actor).on();
				}
			}
			return state;
		}

		public Location[][] locations() {
			Location[][] locs = new Location[height][width];
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					locs[y][x] = new Location(model, x, y);
				}
			}
			return locs;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Actor a : actors) {
				if (a instanceof Light) {
					ret.add(new Move((Light)a));
				}
			}
			return ret;
		}
	}
	
	public class Move implements Action {
		public final Light target;

		public Move(Light target) {
			this.target = target;
		}

		public void apply() {
			target.toggle();
			for(Location other : target.neighbors(ORTHOGONAL)) {
				((Light)model.actorAt(other)).toggle();
			}
		}

		public void undo() {
			apply();
		}
	}
}
