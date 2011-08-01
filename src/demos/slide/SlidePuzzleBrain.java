package demos.slide;

import vitro.*;
import vitro.grid.*;
import java.util.*;

public class SlidePuzzleBrain implements Agent<SlidePuzzle.Gap> {

	Set<String> visited  = new HashSet<String>();
	Stack<Location> path = new Stack<Location>();
	
	public Action choose(SlidePuzzle.Gap actor, Set<Action> options) {
		if (path.size() < 1) {
			visited.clear();
			depthFirstSearch(actor);
			System.out.format("Found path: %s %n", path);
		}
		return actor.move(path.pop(), options);
	}

	boolean depthFirstSearch(SlidePuzzle.Gap actor) {
		String snapshot = snapshot(actor.model());
		if (visited.contains(snapshot)) { return false; }
		visited.add(snapshot);

		if (actor.model().done()) { return true; }
		for(Action a : actor.actions()) {
			a.apply();
			boolean found = depthFirstSearch(actor);
			a.undo();
			if (found) {
				path.push(((MoveAction)a).end);
				return true;
			}
		}
		return false;
	}

	// by building a String representation of the board state
	// we can take advantage of the existing equals() and
	// hashcode() methods.
	String snapshot(SlidePuzzle model) {
		StringBuilder ret = new StringBuilder(model.width * model.height);
		for(int[] row : model.numbers) {
			for(int v : row) { ret.append((char)v); }
		}
		return ret.toString();
	}
}
