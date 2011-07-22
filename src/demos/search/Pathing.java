package demos.search;

import vitro.grid.*;
import vitro.util.*;
import java.util.*;


public class Pathing {

	public List<Location> pathBFS(Search.Domain domain) {
		return path(domain, new LinkedList<Location>());
	}
	
	public List<Location> pathDFS(Search.Domain domain) {
		// note: here we "fix" the linked list to behave as a stack
		return path(domain, new LinkedList<Location>() {
			private static final long serialVersionUID = 1L;
		
			public boolean add(Location elem) {
				super.add(0, elem);
				return true;
			}
		});
	}
	
	public List<Location> pathAStar(Search.Domain domain) {
		return path(domain, new PriorityQueue<Location>(100, new ManhattanComparator(domain.goal)));
	}

	public List<Location> path(Search.Domain domain, Collection<Location> frontier) {
		frontier.add(domain.initial);
		
		Map<Location, Location> visited = new HashMap<Location, Location>();
		visited.put(domain.initial, null);
		
		while(!frontier.isEmpty()) {
			Location current = Groups.first(frontier);
			frontier.remove(current);
			
			if(current.equals(domain.goal)) {
				List<Location> path = new ArrayList<Location>();
				
				Location location = current;
				while(location != null) {
					path.add(0, location);
					location = visited.get(location);
				}
				
				return path;
			}
			
			for(Location next : domain.expand(current)) {
				if(!visited.keySet().contains(next)) {
					frontier.add(next);
					visited.put(next, current);
				}
			}
		}

		return null;
	}
	
	private class ManhattanComparator implements Comparator<Location> {
		public final Location destination;
		
		public ManhattanComparator(Location destination) {
			this.destination = destination;
		}
		
		public int compare(Location l1, Location l2) {
			Integer manDist1 = Math.abs(l1.x - destination.x) + Math.abs(l1.y - destination.y);
			Integer manDist2 = Math.abs(l2.x - destination.x) + Math.abs(l2.y - destination.y);
			
			return manDist1.compareTo(manDist2);
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof ManhattanComparator)) { return false; }
			ManhattanComparator other = (ManhattanComparator)o;
			return other.destination.equals(destination);
		}
	}
	
	private class EuclidianComparator implements Comparator<Location> {
		public final Location destination;
		
		public EuclidianComparator(Location destination) {
			this.destination = destination;
		}
		
		public int compare(Location l1, Location l2) {
			Double manDist1 = (Double)Math.sqrt(Math.pow(l1.x - destination.x, 2) + Math.pow(l1.y - destination.y, 2));
			Double manDist2 = (Double)Math.sqrt(Math.pow(l2.x - destination.x, 2) + Math.pow(l2.y - destination.y, 2));
			
			return manDist1.compareTo(manDist2);
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof EuclidianComparator)) { return false; }
			EuclidianComparator other = (EuclidianComparator)o;
			return other.destination.equals(destination);
		}
	}
}
