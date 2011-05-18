package demos;

import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import java.util.*;

public class WumpusBrain implements Agent<WumpusWorld.Hunter> {

	private static class Room {
		public Set<Room> adjacent = new HashSet<Room>();
		public boolean flapping = false;
		public boolean wind     = false;
		public boolean scent    = false;
		public boolean visited  = false;
	}

	private final Map<Node, Room> worldToPrivate = new HashMap<Node, Room>();
	private final Map<Room, Node> privateToWorld = new HashMap<Room, Node>();

	private final Queue<Edge> path = new LinkedList<Edge>();
	private Room wumpusGoal = null;

	public Action choose(WumpusWorld.Hunter me, Set<Action> options) {

		// we're in an unknown place, so start building a graph
		// and clear any path we may have plotted.
		if (!worldToPrivate.containsKey(me.location())) {
			buildRoom(me.location());
		}

		// update our map with local information:
		Room here = worldToPrivate.get(me.location());
		here.flapping = me.flapping();
		here.wind     = me.wind();
		here.scent    = me.scent();
		here.visited  = true;
			
		// build links to adjacent rooms:
		for(Edge edge : me.location().edges) {
			if (!worldToPrivate.containsKey(edge.end)) {
				buildRoom(edge.end);
			}
			here.adjacent.add(worldToPrivate.get(edge.end));
		}

		// if we wanted to shoot the wumpus in an
		// adjacent room, go for it:
		if (here.adjacent.contains(wumpusGoal)) {
			return me.create(privateToWorld.get(wumpusGoal), WumpusWorld.Arrow.class, options);
		}

		// move along a path I've previously planned:
		if (path.size() > 0) {
			return me.move(path.remove(), options);
		}

		// find likely wumpus locations:
		for(Room room : privateToWorld.keySet()) {
			if (room.visited) { continue; }
			int wumpusCounter = 0;
			Room neighbor = null;
			for(Room other : room.adjacent) {
				if (other.scent) {
					wumpusCounter++;
					neighbor = other;
				}
			}
			if (wumpusCounter >= 2) {
				// plot a path and go,
				// preparing to shoot when ready.
				wumpusGoal = neighbor;
				path.addAll(me.location().path(privateToWorld.get(wumpusGoal)));
				return me.move(path.remove(), options);
			}
		}

		// find safe exploration locations:
		for(Room room : privateToWorld.keySet()) {
			if (room.flapping || room.wind || room.scent) { continue; }
			for(Room other : room.adjacent) {
				if (!other.visited) {
					// plot a path and go.
					path.addAll(me.location().path(privateToWorld.get(room)));
					return me.move(path.remove(), options);
				}
			}
		}

		// find likely bat locations:
		for(Room room : privateToWorld.keySet()) {
			if (room.visited) { continue; }
			int batCounter = 0;
			for(Room other : room.adjacent) {
				if (other.flapping) { batCounter++; }
			}
			if (batCounter >= 2) {
				// plot a path and go.
				path.addAll(me.location().path(privateToWorld.get(room)));
				return me.move(path.remove(), options);
			}
		}

		// give up and walk randomly:
		return Groups.any(Groups.ofType(MoveAction.class, options));
	}

	private void buildRoom(Node place) {
		Room node = new Room();
		worldToPrivate.put(place, node);
		privateToWorld.put(node, place);
	}
}