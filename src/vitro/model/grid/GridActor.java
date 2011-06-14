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

	void move() {}
	void moveToward() {}
	void create() {}
	void destroy() {}
}