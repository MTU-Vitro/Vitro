package demos.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class Search extends Grid {
	public final SearchActor actor;
	public final Target      target;

	//
	public final boolean[][] maze;
	
	/**
	*
	**/
	public Search(boolean[][] maze) {
		super(maze[0].length, maze.length);
		this.maze = maze;
		
		actor  = new SearchActor(this);
		target = new Target(this);
	}

	public boolean done() {
		return actor.location().equals(target.location());
	}

	public boolean passable(Location location) {
		return maze[location.y][location.x];
	}

	public boolean passable(Actor actor, Location location) {
		return passable(location);
	}
	
	public class Domain {
		public final Location initial;
		public final Location goal;
	
		private int count;
		private Map<Location, Integer> expansions;
	
		public Domain(Location initial, Location goal) {
			this.initial = initial;
			this.goal    = goal;
			
			count = 0;
			expansions = new HashMap<Location, Integer>();
		}
		
		public Set<Location> expand(Location location) {
			count += 1;
			expansions.put(location, count);
			
			return model.passableNeighbors(location, model.ORTHOGONAL);
		}
		
		public Map<Location, Integer> expandOrder() {
			return Collections.unmodifiableMap(expansions);
		}
	}
	
	public class SearchActor extends GridActor {
		public SearchActor(Grid model) {
			super(model);
		}
		
		public Domain domain() {
			return new Domain(location(), target.location());
		}
		
		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Location location : passableNeighbors(ORTHOGONAL)) {
				ret.add(new MoveAction(model, location, this));
			}
			return ret;
		}
	}
	
	public class Target extends GridActor { 
		public Target(Grid model) {
			super(model);
		}
	}
}
