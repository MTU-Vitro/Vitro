package assign.search;

import vitro.grid.Location;

public class ManhattanHeuristic implements Heuristic<Location> {
	public final Location goal;

	public ManhattanHeuristic(Location goal) {
		this.goal = goal;
	}

	public double value(Location location) {
		return Math.abs(location.x - goal.x) + Math.abs(location.y - goal.y);
	}
}
