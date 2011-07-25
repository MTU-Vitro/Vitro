package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import java.util.*;
import java.awt.Point;
import static vitro.util.Groups.*;

public class SweeperAgent implements Agent<Sweeper.Player> {
	
	private Set<Point> knownMines = new HashSet<Point>();
	private Set<Point> knownSafe  = new HashSet<Point>();
	private Point lastFlip = new Point(0, 0);

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

		// If we've got safe options, take 'em.
		// Preferentially choose moves that are closest to
		// our previous action.
		if (knownSafe.size() > 0) {
			Point flip = first(knownSafe);
			for(Point safe : knownSafe) {
				if (distance(safe, lastFlip) < distance(flip, lastFlip)) { flip = safe; }
			}
			if (flip != null) { return flip(flip, options); }
		}

		System.out.println("GOTTA GO FAST");

		// Filter out actions that would flip a known mine,
		// and then choose randomly from the remaining options.
		Set<Action> goodIdeas = new HashSet<Action>(options);
		for(Action a : options) {
			if (!(a instanceof Sweeper.FlipAction)) { continue; }
			Sweeper.FlipAction flip = (Sweeper.FlipAction)a;
			if (!knownMines.contains(new Point(flip.location.x, flip.location.y))) {
				goodIdeas.add(flip);
			}
		}
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

	private double distance(Point a, Point b) {
		int dx = a.x - b.x;
		int dy = a.y - b.y;
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	private Sweeper.FlipAction flip(Point p, Set<Action> options) {
		for(Action a : options) {
			Sweeper.FlipAction flip = (Sweeper.FlipAction)a;
			if (flip.location.x == p.x && flip.location.y == p.y) {
				lastFlip = p;
				return flip;
			}
		}
		return null;
	}
}