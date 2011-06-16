package vitro.grid;

import vitro.*;
import java.util.*;

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
		return model.passable(this, ret);
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

	void move() {}
	void moveToward() {}
	void create() {}
	void destroy() {}
}