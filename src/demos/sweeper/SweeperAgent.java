package demos.sweeper;

import vitro.*;
import java.util.*;
import java.awt.Point;
import static vitro.util.Groups.*;

public class SweeperAgent implements Agent<Sweeper.Player> {
	
	private Set<Point> knownMines = new HashSet<Point>();
	private Set<Point> knownSafe  = new HashSet<Point>();

	public Action choose(Sweeper.Player actor, Set<Action> options) {

		// Abandon our previous assumptions.
		knownMines.clear();
		knownSafe.clear();

		// If a space indicating N mines is
		// surrounded by exactly N spaces,
		// all those spaces are mines.
		for(int y = 0; y < actor.height(); y++) {
			for(int x = 0; x < actor.width(); x++) {
				if (actor.hidden(x, y)) { continue; }
				findMines(x, y, actor);
			}
		}

		// If a count can be explained by known mines,
		// we know it's a safe choice.
		for(int y = 0; y < actor.height(); y++) {
			for(int x = 0; x < actor.width(); x++) {
				if (actor.hidden(x, y)) { continue; }
				findClear(x, y, actor);
			}
		}

		//System.out.println("Known Mines: "+knownMines);
		//System.out.println("Known Safe:  "+knownSafe);

		// If we've got safe options, take 'em.
		if (knownSafe.size() > 0) {
			return flip(first(knownSafe), options);
		}

		// give up and pick randomly.
		// TODO: make this exclude options from 'known mines'
		// so as to make it slightly less suicidal.
		System.out.println("GOTTA GO FAST");
		return any(options);
	}

	private void findMines(int x, int y, Sweeper.Player actor) {
		List<Point> cells = new ArrayList<Point>();
		for(int[] delta : vitro.grid.Grid.ADJACENT) {
			int cx = x + delta[0];
			int cy = y + delta[1];
			if (cx < 0 || cy < 0 || cx >= actor.width() || cy >= actor.height()) { continue; }
			if (actor.hidden(cx, cy)) { cells.add(new Point(cx, cy)); }
		}
		if (actor.count(x, y) == cells.size()) { knownMines.addAll(cells); }
	}

	private void findClear(int x, int y, Sweeper.Player actor) {
		int dangerous = 0;
		List<Point> safe = new ArrayList<Point>();
		for(int[] delta : vitro.grid.Grid.ADJACENT) {
			int cx = x + delta[0];
			int cy = y + delta[1];
			if (cx < 0 || cy < 0 || cx >= actor.width() || cy >= actor.height()) { continue; }
			Point here = new Point(cx, cy);
			if (knownMines.contains(here)) { dangerous++;    }
			else if (actor.hidden(cx, cy)) { safe.add(here); }
		}
		if (actor.count(x, y) == dangerous) { knownSafe.addAll(safe); }
	}

	private Sweeper.FlipAction flip(Point p, Set<Action> options) {
		for(Action a : options) {
			if (!(a instanceof Sweeper.FlipAction)) { continue; }
			Sweeper.FlipAction flip = (Sweeper.FlipAction)a;
			if (flip.location.x == p.x && flip.location.y == p.y) { return flip; }
		}
		return null;
	}
}