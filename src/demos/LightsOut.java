package demos;

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
			}
		}
	}

	public class Light extends GridActor implements Factional {
		private int team;
		public Light(Grid model) { super(model); }
		public int team()        { return team;  }
		public boolean on()      { return team == 1; }
		private void toggle()    { team = team == 0 ? 1 : 0; }
	}

	public class Player extends Actor {
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
		private final Light target;
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