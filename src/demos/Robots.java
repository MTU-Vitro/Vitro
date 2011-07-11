package demos;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class Robots extends Grid {
	private static int BlockId = 0;
	private static int BLUId   = 0;
	private static int RNGId   = 0;

	public static final int SOLID       = 0;
	public static final int LIGHT       = 1;
	public static final int TARGET      = 2;
	public static final int DARK        = 3;
	public static final int SLUDGE      = 4;
	public static final int DARK_SLUDGE = 5;

	public final int[][] tiles;

	public Robots(int[][] tiles) {
		super(tiles[0].length, tiles.length);
		this.tiles = tiles;
	}

	public boolean dark(Location location) {
		int tile = tiles[location.y][location.x];
		return tile == DARK || tile == DARK_SLUDGE;
	}

	public boolean sludge(Location location) {
		int tile = tiles[location.y][location.x];
		return tile == SLUDGE || tile == DARK_SLUDGE;
	}

	public boolean passable(Actor actor, Location location) {
		// out of bounds is impassible:
		if (location == null || !location.valid()) { return false; }		

		// RNG can hover over the same square as different objects:
		Actor other = actorAt(location);
		if (other != null) {
			if ( (other instanceof RNG) &&  (actor instanceof RNG)) { return false; }
			if (!(other instanceof RNG) && !(actor instanceof RNG)) { return false; }
		}

		// use the supplied collision map:
		return tiles[location.y][location.x] != SOLID;
	}

	public boolean done() {
		// every target must be covered by a Block or a BLU.
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles[0].length; x++) {
				if (tiles[y][x] == TARGET) {
					Actor actor = actorAt(new Location(this, x, y));
					if (actor == null) { return false; }
					if (!(actor instanceof Block) && !(actor instanceof BLU)) { return false; }
				}
			}
		}
		return true;
	}

	public RNG   createRNG()   { return new RNG(this); }
	public BLU   createBLU()   { return new BLU(this); }
	public Block createBlock() { return new Block();   }

	public class Block extends Actor {
		private final int id;

		public Block() {
			id = BlockId++;
		}

		public String toString() {
			return String.format("Block %d", id);
		}
	}

	public static class RoboMove extends MoveAction {
		private DestroyAction sploosh;

		public RoboMove(Robots model, Location destination, Actor actor) {
			super(model, destination, actor);
			if (model.sludge(destination)) {
				if (!(actor instanceof RNG) || model.dark(destination)) {
					// if a robot is taking a dip in sludge,
					// we want the DestroyAction to reflect
					// the destination location:
					super.apply();
					sploosh = new DestroyAction(model, actor);
					super.undo();
				}
			}
		}

		public void apply() {
			super.apply();
			if (sploosh != null) { sploosh.apply(); }
		}

		public void undo() {
			if (sploosh != null) { sploosh.undo(); }
			super.undo();
		}

		public String toString() {
			return super.toString() + ((sploosh == null) ? "" : ".. landing in a puddle of goo.");
		}
	}

	public static class PushAction implements Action {
		private final Robots grid;
		public final Actor pusher;
		public final Actor pushed;
		public final Location source;
		public final Location pushedFrom;
		public final Location pushedTo;
		
		public PushAction(Robots grid, Actor pusher, Actor pushed) {
			this.grid = grid;
			this.pusher = pusher;
			this.pushed = pushed;
			source     = grid.locations.get(pusher);
			pushedFrom = grid.locations.get(pushed);
			pushedTo   = grid.pushDestination(pusher, pushed);
		}

		public void apply() {
			grid.locations.put(pushed, pushedTo);
			grid.locations.put(pusher, pushedFrom);
		}

		public void undo() {
			grid.locations.put(pushed, pushedFrom);
			grid.locations.put(pusher, source);
		}

		public String toString() {
			return String.format("Push '%s' to %s with actor '%s'.", pushed, pushedTo, pusher);
		}
	}

	private Location pushDestination(Actor pusher, Actor pushed) {
		Location a = locations.get(pusher);
		Location b = locations.get(pushed);
		Location ret = null;
		if (a == null || b == null) { return null; }
		if      (a.x > b.x && a.y == b.y) { ret = new Location(this, b.x-1, b.y); } // left
		else if (a.x < b.x && a.y == b.y) { ret = new Location(this, b.x+1, b.y); } // right
		else if (a.y > b.y && a.x == b.x) { ret = new Location(this, b.x, b.y-1); } // up
		else if (a.y < b.y && a.x == b.x) { ret = new Location(this, b.x, b.y+1); } // down
		if (passable(pushed, ret)) { return ret; }
		return null;
	}

	public static class DropAction extends CreateAction {
		public final RNG rng;

		public DropAction(Grid model, RNG actor) {
			super(model, actor.location(), actor.carried);
			this.rng = actor;
		}

		public void apply() {
			super.apply();
			rng.carried = null;
		}

		public void undo() {
			super.undo();
			rng.carried = actor;
		}

		public String toString() {
			return String.format("Drop '%s' with actor '%s'.", actor, rng);
		}
	}

	public static class LiftAction extends DestroyAction {
		public final RNG rng;
		public final Actor target;

		public LiftAction(Grid model, RNG actor, Actor target) {
			super(model, target);
			this.rng    = actor;
			this.target = target;
		}

		public void apply() {
			super.apply();
			rng.carried = target;
		}

		public void undo() {
			super.undo();
			rng.carried = null;
		}

		public String toString() {
			return String.format("Lift '%s' with actor '%s'.", target, rng);
		}
	}

	public abstract class Robot extends GridActor {
		public Robot(Robots model) { super(model); }
	}

	public class BLU extends Robot {
		private final int id;

		public BLU(Robots model) {
			super(model);
			id = BLUId++;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			if (zonked()) { return ret; }
			for(Location location : neighbors(ORTHOGONAL)) {
				if (model.passable(this, location)) {
					ret.add(new RoboMove((Robots)model, location, this));
				}
				// BLU can push objects:
				Actor other = model.actorAt(location);
				if (pushDestination(this, other) != null) {
					ret.add(new PushAction((Robots)model, this, other));
				}
			}
			return ret;
		}

		public String toString() {
			return String.format("BLU %d", id);
		}
	}

	public class RNG extends Robot {
		private final int id;
		public Actor carried = null;

		public RNG(Robots model) {
			super(model);
			id = RNGId++;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			// RNG is solar-powered and becomes inactive in the dark.
			if (((Robots)model).dark(location())) { return ret; }

			for(Location location : neighbors(ORTHOGONAL)) {
				if (model.passable(this, location)) {
					ret.add(new MoveAction(model, location, this));
				}
			}
			// RNG can drop objects:
			if (carried != null && model.passable(carried, location())) {
				ret.add(new DropAction(model, this));
			}
			// RNG can pick up objects:
			if (carried == null) {
				Set<Actor> targets = model.actorsAt(location());
				for(Actor target : targets) {
					if (target instanceof RNG) {
						// this excludes both yourself and
						// other operational (flying) RNGs
						if (!((RNG)target).zonked()) { continue; }
					}
					ret.add(new LiftAction(model, this, target));
				}
			}
			return ret;
		}

		public String toString() {
			return String.format("RNG %d", id);
		}
	}
}