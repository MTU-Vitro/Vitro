package demos.wumpus;

import vitro.*;
import vitro.graph.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

/* */
public class LogicalWumpusBrain implements Agent<WumpusWorld.Hunter> {

	// mappings between model and internal structures
	private final ReversibleMap<Node, Room> worldToPrivate = new ReversibleMap<Node, Room>();
	private final ReversibleMap<Room, Node> privateToWorld = worldToPrivate.reverse();
	
	// knowledge bases for logical inference
	private BoolTable wumpusKB = new BoolTable();
	private BoolTable batKB    = new BoolTable();
	private BoolTable pitKB    = new BoolTable();
	
	// current decision
	Decision decision = null;


	/* */
	public Action choose(WumpusWorld.Hunter hunter, Set<Action> options) {
		System.out.format("%n%n");
		updateInternal(hunter);
		Room here = worldToPrivate.get(hunter.location());
		
		// check to see if we need to make a new decision
		if(decision != null && !decision.done(options)) {
			return decision.step(options);
		}
		
		// kill any adjacent, known wumpuses
		for(Room wumpus : here.adjacent) {
			if(wumpusKB.known(wumpus) && wumpusKB.evaluate(wumpus)) {
				System.out.println("DIE!");
				decision = new KillDecision(hunter, wumpus);
				return decision.step(options);
			}
		}
		
		// find any known wumpuses to kill
		for(Room wumpus : privateToWorld.keySet()) {
			if(wumpusKB.known(wumpus) && wumpusKB.evaluate(wumpus)) {
				Set<Room> safeRooms = safeRooms();
				safeRooms.add(wumpus);
				
				List<Room> path = path(here, wumpus, safeRooms);
				path.remove(wumpus);
				System.out.println("I've got it!");
				decision = new PathDecision(hunter, path);
				return decision.step(options);
			}
		}

		// find safe rooms to explore
		{
			System.out.println(safeRooms());
			List<Room> path = path(here, safeRooms());
			if(path != null) {
				System.out.println("Walking!");
				decision = new PathDecision(hunter, path);
				return decision.step(options);
			}
		}
		
		// find bats to exploit
		for(Room bat : privateToWorld.keySet()) {
			if(batKB.known(bat) && batKB.evaluate(bat)) {
				Set<Room> safeRooms = safeRooms();
				safeRooms.add(bat);
				System.out.println("Find a Bat!");
				List<Room> path = path(here, bat, safeRooms);
				return decision.step(options);
			}
		}
		
		// otherwise, take your 3 chances... they're only arrows!
		decision = new KillDecision(hunter, Groups.any(here.adjacent));
		return decision.step(options);
	}


	// internal decision making mechanism
	private interface Decision {
		public boolean done(Set<Action> options);
		public Action  step(Set<Action> options);
	}
	
	private class KillDecision implements Decision {
		private final WumpusWorld.Hunter hunter;
		private final Room target;
		
		private int count = 0;
		
		public KillDecision(WumpusWorld.Hunter hunter, Room target) {
			this.hunter    = hunter;
			this.target    = target;
		}
		
		public boolean done(Set<Action> options) {
			// TODO verify still valid
			return count == 2;
		}
		
		public Action step(Set<Action> options) {
			count++;
			
			if(count == 1) {
				BoolTable newWumpusKB = new BoolTable();
				for(Room room : privateToWorld.keySet()) {
					if(wumpusKB.known(room) && wumpusKB.evaluate(room) == false) {
						newWumpusKB.assign(room, false);
					}
					if(!wumpusKB.known(room)) {
						System.out.format("Suspicious Room: %s!%n", room);
						for(Room adj : room.adjacent) {
							System.out.format("   Resetting: %s!%n", adj);
							room.visited = false;
						}
					}
				}
				wumpusKB = newWumpusKB;
			}
			if(count == 1) {
				return hunter.create(privateToWorld.get(target), WumpusWorld.Arrow.class, options);
			}
			
			return null;
		}
	}
	
	private class PathDecision implements Decision {
		private final WumpusWorld.Hunter hunter;
		private final List<Room> path;
		
		public PathDecision(WumpusWorld.Hunter hunter, List<Room> path) {
			this.hunter = hunter;
			this.path   = path;
		}
	
		public boolean done(Set<Action> options) {
			// TODO verify still valid
			return path == null || path.isEmpty();
		}
		
		public Action step(Set<Action> options) {
			Room here = worldToPrivate.get(hunter.location());

			if(path == null || path.isEmpty()) { return null; }
			Room next = path.remove(0);
			return hunter.move(privateToWorld.get(next), options);
		}
	}
	

	//
	private void updateInternal(WumpusWorld.Hunter hunter) {
		// initialization (unknown -> start building internal representation)
		if(!worldToPrivate.containsKey(hunter.location())) {
			worldToPrivate.put(hunter.location(), new Room());
		}

		// update local information
		Room here = worldToPrivate.get(hunter.location());
		here.visited = true;
		
		wumpusKB.assign(here, false);
		batKB.assign(here, false);
		pitKB.assign(here, false);

		// update adjacent rooms
		for(Edge edge : hunter.location().edges) {
			if (!worldToPrivate.containsKey(edge.end)) {
				worldToPrivate.put(edge.end, new Room());
			}
			here.adjacent.add(worldToPrivate.get(edge.end));
			worldToPrivate.get(edge.end).adjacent.add(here);
		}

		// update knowledge base regarding adjacent rooms
		boolean rebuild = false;
		for(Room room : here.adjacent) {
			if(!hunter.scent() && wumpusKB.conflicts(room, false)) {
				rebuild = true;
			}
		}
		if(rebuild) {
		/*
			BoolTable newWumpusKB = new BoolTable();
			for(Room room : privateToWorld.keySet()) {
				if(wumpusKB.known(room) && wumpusKB.evaluate(room) == false) {
					newWumpusKB.assign(room, false);
				}
				if(!wumpusKB.known(room)) {
					System.out.format("Suspicious Room: %s!%n", room);
					for(Room adj : room.adjacent) {
						System.out.format("   Resetting: %s!%n", adj);
						room.visited = false;
					}
				}
			}
			wumpusKB = newWumpusKB;
		*/
		}
		
		for(Room room : here.adjacent) {
			if(!hunter.scent()   ) { wumpusKB.assign(room, false); }
			if(!hunter.flapping()) { batKB.assign(room, false);    }
			if(!hunter.wind()    ) { pitKB.assign(room, false);    }
		}
		
		Map<Room, Boolean> possible = new HashMap<Room, Boolean>();
		for(Room room : here.adjacent) {
			possible.put(room, true);
		}
		if(hunter.scent()   ) { wumpusKB.assign(possible); }
		if(hunter.flapping()) { batKB.assign(possible);    }
		if(hunter.wind()    ) { pitKB.assign(possible);    }
		
		System.out.format("Wumpus KB : %s%n", wumpusKB);
		System.out.format("Bat KB    : %s%n", batKB);
		System.out.format("Pit KB    : %s%n", pitKB);
	}
	
	
	// internal graph structure with mappings to the real structure
	private class Room {
		public Set<Room> adjacent = new HashSet<Room>();
		public boolean visited    = false;
	}
	
	private boolean safe(Room room) {
		if(!wumpusKB.known(room) || wumpusKB.evaluate(room)) { return false; }
		if(!batKB.known(room)    || batKB.evaluate(room)   ) { return false; }
		if(!pitKB.known(room)    || pitKB.evaluate(room)   ) { return false; }
		return true;
	}
	
	private Set<Room> safeRooms() {
		Set<Room> safeRooms = new HashSet<Room>();
		for(Room room : privateToWorld.keySet()) {
			if(safe(room)) { safeRooms.add(room); }
		}
		return safeRooms;
	}
	
	
	// pathing
	private List<Room> path(Room start, Room destination, Set<Room> travelable) {
		Queue<Room> frontier = new LinkedList<Room>();
		frontier.add(start);

		Map<Room, Room> visited = new HashMap<Room, Room>();
		visited.put(start, null);

		while(!frontier.isEmpty()) {
			Room current = frontier.poll();
			System.out.println("\n---\n" + frontier + "\n\n" + travelable + "\n---\n");

			if((destination == null && current.visited == false) || current == destination) {
				List<Room> path = new ArrayList<Room>();
				
				Room next = current;
				while(visited.get(next) != null) {
					path.add(0, next);
					next = visited.get(next);
				}

				System.out.println("Returning: " + path);
				return path;
			}

			for(Room next : current.adjacent) {
				if(!visited.containsKey(next) && travelable.contains(next)) {
					frontier.add(next);
					visited.put(next, current);
				}
			}
		}

		System.out.println("Returning: null");
		return null;
	}
	
	private List<Room> path(Room start, Set<Room> travelable) {
		return path(start, null, travelable);
	}
}
