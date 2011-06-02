package demos;

import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import java.util.*;
import static vitro.util.Groups.*;

/* */
public class LogicalWumpusBrain implements Agent<WumpusWorld.Hunter> {

	//
	private final ReversibleMap<Node, Room> worldToPrivate = new ReversibleMap<Node, Room>();
	private final ReversibleMap<Room, Node> privateToWorld = worldToPrivate.reverse();
		
	private final Map<LogicType, KnowledgeBase> knowledge = new HashMap<LogicType, KnowledgeBase>();
	
	//
	private List<Room> targetPath = null;

	/* */
	public Action choose(WumpusWorld.Hunter hunter, Set<Action> options) {
		updateInternal(hunter);
		
		Room here = worldToPrivate.get(hunter.location());
		
		// check to see if we already have a decision
		if(targetPath != null && !targetPath.isEmpty()) {
			return moveAlong(hunter, options, targetPath);
		}
		targetPath = null;
		
		// build useful data structures
		Set<Room> visitedRooms = new HashSet<Room>();
		Set<Room> safeRooms    = new HashSet<Room>();
		for(Room room : privateToWorld.keySet()) {
			if(room.visited) { visitedRooms.add(room); }
			if(isSafe(room)) { safeRooms.add(room);    }
		}
		
		// find any wumpuses
		{
			for(Room room : here.adjacent) {
				if(hasType(LogicType.Wumpus, room) && !hunter.whistle()) {
					return hunter.create(privateToWorld.get(room), WumpusWorld.Arrow.class, options);
				}
			}
			for(Room room : privateToWorld.keySet()) {
				if(hasType(LogicType.Wumpus, room)) {
					Set<Room> rooms = new HashSet<Room>(safeRooms);
					rooms.add(room);
					targetPath = path(here, room, rooms);
					targetPath.remove(room);
					return moveAlong(hunter, options, targetPath);
				}
			}
		}
		
		// find safe rooms to explore for exploration
		{
			Set<Room> rooms = new HashSet<Room>(safeRooms);
			rooms.removeAll(visitedRooms);
			
			if(!rooms.isEmpty()) {
				targetPath = path(here, Groups.any(rooms), safeRooms);
				return moveAlong(hunter, options, targetPath);
			}
		}
		
		// otherwise give up
		return hunter.create(privateToWorld.get(Groups.any(here.adjacent)), WumpusWorld.Arrow.class, options);
	}

	/* */
	private void updateInternal(WumpusWorld.Hunter me) {

		// initialization (unknown -> start building internal representation)
		if(!worldToPrivate.containsKey(me.location())) {
			worldToPrivate.put(me.location(), new Room());
				
			knowledge.put(LogicType.Wumpus, new KnowledgeBase());
			knowledge.put(LogicType.Bat   , new KnowledgeBase());
			knowledge.put(LogicType.Pit   , new KnowledgeBase());
		}

		// update local information
		Room here = worldToPrivate.get(me.location());
		here.visited = true;
			
		knowledge.get(LogicType.Wumpus).add(new Clause(new Literal(true, here)));
		knowledge.get(LogicType.Bat   ).add(new Clause(new Literal(true, here)));
		knowledge.get(LogicType.Pit   ).add(new Clause(new Literal(true, here)));

		// update adjacent rooms
		for(Edge edge : me.location().edges) {
			if (!worldToPrivate.containsKey(edge.end)) {
				worldToPrivate.put(edge.end, new Room());
			}
			here.adjacent.add(worldToPrivate.get(edge.end));
			worldToPrivate.get(edge.end).adjacent.add(here);
		}

		// update knowledge base regarding adjacent rooms
		for(Room room : here.adjacent) {
			if(!me.scent()   ) { knowledge.get(LogicType.Wumpus).add(new Clause(new Literal(true, room))); }
			if(!me.flapping()) { knowledge.get(LogicType.Bat   ).add(new Clause(new Literal(true, room))); }
			if(!me.wind()    ) { knowledge.get(LogicType.Pit   ).add(new Clause(new Literal(true, room))); }
		}
		
		Clause surroundings = new Clause();
		for(Room room : here.adjacent) {
			surroundings.add(new Literal(false, room));
		}
		if(me.scent()   ) { knowledge.get(LogicType.Wumpus).add(surroundings); }
		if(me.flapping()) { knowledge.get(LogicType.Bat   ).add(surroundings); }
		if(me.wind()    ) { knowledge.get(LogicType.Pit   ).add(surroundings); }
	}
	
	/* */
	private boolean hasType(LogicType type, Room room) {
		return knowledge.get(type).query(new Literal(false, room));
	}
	
	private boolean hasNoType(LogicType type, Room room) {
		return knowledge.get(type).query(new Literal(true, room));
	}
	
	private boolean isSafe(Room room) {
		for(LogicType type : LogicType.values()) {
			if(!hasNoType(type, room)) {
				return false;
			}
		}
		return true;
	}
	
	/* */
	private MoveAction moveAlong(WumpusWorld.Hunter hunter, Set<Action> options, List<Room> path) {
		Room here = worldToPrivate.get(hunter.location());
		
		if(path == null || path.isEmpty()) { return null; }
		Room next = path.remove(0);
		return hunter.move(privateToWorld.get(next), options);
	}
	
	/* */
	private List<Room> path(Room start, Room destination, Set<Room> travelable) {
		Queue<Room> frontier = new LinkedList<Room>();
		frontier.add(start);

		Map<Room, Room> visited = new HashMap<Room, Room>();
		visited.put(start, null);

		while(!frontier.isEmpty()) {
			Room current = frontier.poll();

			if(current == destination) {
				List<Room> path = new ArrayList<Room>();
				
				Room next = destination;
				while(visited.get(next) != null) {
					path.add(0, next);
					next = visited.get(next);
				}

				return path;
			}

			for(Room next : current.adjacent) {
				if(!visited.containsKey(next) && travelable.contains(next)) {
					frontier.add(next);
					visited.put(next, current);
				}
			}
		}

		return null;
	}
	
	private List<Room> path(Room start, Room destination) {
		return path(start, destination, privateToWorld.keySet());
	}
	
	/* */
	private class Room {
		public Set<Room> adjacent = new HashSet<Room>();
		public boolean visited    = false;
	}
	
	/* */
	private enum LogicType { Wumpus, Bat, Pit };
	
	/* */
	private class Literal {
		public final boolean negated;
		public final Object  symbol;
		
		public Literal(boolean negated, Object symbol) {
			this.negated = negated;
			this.symbol  = symbol;
		}
		
		public Literal opposite() {
			return new Literal(!negated, symbol);
		}
		
		public boolean isOpposite(Literal literal) {
			return symbol.equals(literal.symbol) && (negated ^ literal.negated);
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Literal)) { return false; }
				Literal other = (Literal)o;
				return (other.negated == this.negated) && (other.symbol.equals(this.symbol));
		}
		
		@Override
		public int hashCode() {
			return symbol.hashCode();
		}
		
		@Override
		public String toString() {
			return (negated ? "Not " : "") + symbol.toString();
		}
	}
				
	/* */
	private class Clause extends HashSet<Literal> {
		private static final long serialVersionUID = 1L;
			
		public Clause() { 
			super();
		}
			
		public Clause(Collection<? extends Literal> c) { 
			super(c);
		}
			
		public Clause(Literal... literals) {
			super();
				
			for(Literal literal : literals) { add(literal); }
		}
		
		public Clause resolve(Clause clause) {
			Clause ret  = new Clause(this);
			Clause copy = new Clause(clause);
				
			for(Literal literal : clause) {
				Literal opposite = literal.opposite();
				if(ret.contains(opposite)) {
					ret.remove(opposite);
					copy.remove(literal);
				}
			}
				
			ret.addAll(copy);
			return ret;
		}
			
		public Clause resolve(Literal literal) {
			Clause ret = new Clause(this);
			
			ret.remove(literal.opposite());
			return ret;
		}
	}
	
	/* */
	private class KnowledgeBase extends HashSet<Clause> {
		private static final long serialVersionUID = 1L;
		
		public boolean query(Literal literal) {
			simplify();
			if(contains(new Clause(literal))) { return true; }
			
			// resolution proof
			return false;
		}
		
		public void simplify() {
			Set<Literal> singletons = new HashSet<Literal>();
			for(Clause clause : this) {
				if(clause.size() == 1) {
					singletons.add(Groups.first(clause));
				}
			}
			
			Set<Clause> clauses = new HashSet<Clause>(this);
			for(Clause clause : clauses) {
				this.remove(clause);
				for(Literal singleton : singletons) {
					clause.remove(singleton.opposite());
				}
				this.add(clause);
			}
		}
	}
}
