package demos.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;
import java.util.*;

public class SokobanDomainBLU implements Domain<SokobanStateBLU> {
	protected final Robots.BLU      blu;
	protected final Location        block;
	protected final SokobanStateBLU initial;
	protected final Location goal;

	public SokobanDomainBLU(Robots.BLU blu, Location block, SokobanStateBLU initial, Location goal) {
		this.blu     = blu;
		this.block   = block;
		this.initial = initial;
		this.goal    = goal;
	}
	
	public SokobanStateBLU initial() {
		return initial;
	}
	
	public boolean isGoal(SokobanStateBLU state) {
		return goal.equals(state.blockLocation);
	}
	
	public Set<SokobanStateBLU> expand(SokobanStateBLU state) {
		Set<SokobanStateBLU> ret = new HashSet<SokobanStateBLU>();

		Location current = state.blockLocation;

		Set<Location> locations = blu.passableNeighbors(state.blockLocation, Grid.ORTHOGONAL);
		if(blu.neighbors(state.blockLocation, Grid.ORTHOGONAL).contains(block)) { locations.add(block); }

		for(Location next : locations) {
			Location toPush = current.add(current.x - next.x, current.y - next.y);
		
			Search<Location> method = new BreadthFirstSearch<Location>();
			Domain<Location> domain = new SubDomainBLU(blu, block, state.bluLocation, toPush);

			List<Location> path = method.search(domain);
			if(path != null) {
				ret.add(new SokobanStateBLU((double)(path.size()) + state.cost, toPush, next));
			}
		}
		return ret;
	}
}

class SubDomainBLU extends DomainBLU {
	protected final Location block;

	public SubDomainBLU(Robots.BLU blu, Location block, Location initial, Location goal) {
		super(blu, initial, goal);
		this.block   = block;
	}
	
	public Set<Location> expand(Location location) {
		Set<Location> neighbors = blu.passableNeighbors(location, Robots.ORTHOGONAL);
		if(blu.neighbors(location, Robots.ORTHOGONAL).contains(block)) { neighbors.add(block); }
		return neighbors;
	}
}
