package vitro.grid;

import vitro.*;
import java.util.*;
import static vitro.util.Groups.*;

public class GridActor extends Actor {
	
	protected final Grid model;

	public GridActor(Grid model) {
		this.model = model;
	}

	public Location location() {
		return model.locations.get(this);
	}

	public Set<Location> neighbors(int[][] deltas) {
		Set<Location> ret = new HashSet<Location>();
		for(int[] d : deltas) {
			int nx = location().x + d[0];
			int ny = location().y + d[1];
			if (nx >= 0 && nx < model.width && ny >= 0 && ny < model.height) {
				ret.add(new Location(model, nx, ny));
			}
		}
		return ret;
	}

	public Set<Location> passableNeighbors(int[][] deltas) {
		return model.passable(this, neighbors(deltas));
	}

	public Set<Location> pumpingNeighbors(int[][] deltas) {
		Set<Location> ret = new HashSet<Location>();
		for(int[] d : deltas) {
			int x = location().x;
			int y = location().y;
			while(true) {
				x += d[0];
				y += d[1];
				if (x < 0 || x >= model.width || y < 0 || y >= model.height) { break; }
				Location location = new Location(model, x, y);
				if (!model.passable(this, location)) { break; }
				ret.add(new Location(model, x, y));
			}
		}
		return ret;
	}

	public Set<Action> moves(Set<Location> locations) {
		Set<Action> ret = new HashSet<Action>();
		for(Location location : locations) {
			ret.add(new MoveAction(model, location, this));
		}
		return ret;
	}

	public MoveAction move(Location location, Set<Action> options) {
		for(Action action : ofType(MoveAction.class, options)) {
			MoveAction move = (MoveAction)action;
			if (move.actor == this && move.end.equals(location)) { return move; }
		}
		return null;
	}

	void moveToward() {}
	void create() {}
	void destroy() {}
}