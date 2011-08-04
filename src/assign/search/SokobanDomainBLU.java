package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;
import java.util.*;

public class SokobanDomainBLU implements Domain<SokobanStateBLU> {
	protected final Robots.BLU      blu;
	protected final SokobanStateBLU initial;
	protected final Location goal;

	public SokobanDomainBLU(Robots.BLU blu, SokobanStateBLU initial, Location goal) {
		this.blu     = blu;
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
		for(Location next : blu.passableNeighbors(state.blockLocation, Grid.ORTHOGONAL)) {
			Location toPush = current.add(current.x - next.x, current.y - next.y);
		
			Search<Location> method = new BreadthFirstSearch<Location>();
			Domain<Location> domain = new DomainBLU(blu, state.bluLocation, toPush);
		
			if(method.search(domain) != null) {
				ret.add(new SokobanStateBLU(toPush, next));
			}
		}
		return ret;
	}
}
