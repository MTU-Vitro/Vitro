package demos.wumpus;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import java.awt.Color;

public class LogicalWumpusGridBrain implements Agent<WumpusGrid.Hunter>, Annotated {
	WumpusGrid.Hunter hunter;
	Grid grid;

	public LogicalWumpusGridBrain(Grid g) {
		grid = g;
	}

	@Override
	public Action choose(WumpusGrid.Hunter hunter, Set<Action> options) {
		this.hunter = hunter;
		
		return null;
	}
	
	@Override
	public Set<Annotation> annotations() {
		Map<Location, Integer> coloring = new HashMap<Location, Integer>();
		
		Collection<Node> frontier = new ArrayList<Node>();
		Collection<Node> visited  = new HashSet<Node>();
		
		frontier.add(new Node(hunter.location(), 0));
		while(!frontier.isEmpty()) {
			Node node = Groups.first(frontier);
			
			frontier.remove(node);
			visited.add(node);
			
			float scale = 1.0f / node.depth;
			coloring.put(node.location, node.depth);
			
			for(Location neighbor : grid.neighbors(node.location, grid.ORTHOGONAL)) {
				Node next = new Node(neighbor, node.depth + 1);
				if(!visited.contains(next) && !frontier.contains(next)) {
					frontier.add(next);
				}
			}
		}
			
	
		Set<Annotation> ret = new HashSet<Annotation>();
		//ret.add(new GridAnnotation(coloring, new Color(1.0f, 1.0f, 1.0f), new Color(0.0f, 0.0f, 1.0f)));
		//ret.add(new GridAnnotation(coloring, new Color(1.0f, 0.0f, 0.0f), new Color(1.0f, 1.0f, 1.0f)));
		ret.add(new GridAnnotation(coloring, new Color(1.0f, 0.0f, 0.0f), new Color(0.0f, 0.0f, 1.0f)));
		return ret;
	}
	
	private static class Node {
		public final Location location;
		public final int      depth;
		
		public Node(Location location, int depth) {
			this.location = location;
			this.depth    = depth;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Node)) { return false; }
			Node other = (Node)o;
			return location.equals(other.location);
		}
		
		@Override
		public int hashCode() {
			return location.hashCode();
		}
	}
}
