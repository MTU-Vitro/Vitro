package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;
import java.util.*;

public class DomainBLU implements Domain<Location> {
	protected final Robots.BLU blu;
	protected final Location   initial;
	protected final Location   goal;

	public DomainBLU(Robots.BLU blu, Location initial, Location goal) {
		this.blu     = blu;
		this.initial = initial;
		this.goal    = goal;
	}
	
	public Location initial() {
		return initial;
	}
	
	public boolean isGoal(Location l) {
		return goal.equals(l);
	}
	
	public double cost(Location head, Location tail) {
		return 1.0;
	}
	
	public Set<Location> expand(Location location) {
		return blu.passableNeighbors(location, Robots.ORTHOGONAL);
	}
}
