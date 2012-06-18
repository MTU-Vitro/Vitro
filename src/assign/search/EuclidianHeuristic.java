package assign.search;

import vitro.grid.Location;

public class EuclidianHeuristic implements Heuristic<Location> {
	public final Location goal;

	public EuclidianHeuristic(Location goal) {
		this.goal = goal;
	}

	public double value(Location location) {
		return (Double)Math.sqrt(Math.pow(location.x - goal.x, 2) + Math.pow(location.y - goal.y, 2));
	}
}
