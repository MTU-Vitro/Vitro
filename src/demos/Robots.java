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
	public static final int DARK        = 2;
	public static final int TARGET      = 3;
	public static final int DARK_TARGET = 4;
	public static final int SLUDGE      = 5;
	public static final int DARK_SLUDGE = 6;
	public static final int FLOAT       = 7;
	public static final int DARK_FLOAT  = 8;

	public final int[][] tiles;

	public Robots(int[][] tiles) {
		super(tiles[0].length, tiles.length);
		this.tiles = tiles;
	}

	public boolean dark(Location location) {
		int tile = tiles[location.y][location.x];
		return tile == DARK || tile == DARK_SLUDGE || tile == DARK_TARGET;
	}

	public boolean sludge(Location location) {
		int tile = tiles[location.y][location.x];
		return tile == SLUDGE || tile == DARK_SLUDGE;
	}

	public boolean target(Location location) {
		int tile = tiles[location.y][location.x];
		return tile == TARGET || tile == DARK_TARGET;
	}

	public boolean passable(Actor actor, Location location) {
		// out of bounds is impassible:
		if (location == null || !location.valid()) { return false; }		

		// RNG can hover over the same square as different objects:
		Actor other = actorAt(location);
		if (other != null) {
			if ( (other instanceof RNG) &&  (actor instanceof RNG)) { return  dark(location); }
			if ( (other instanceof RNG) && !(actor instanceof RNG)) { return !dark(location); }
			if (!(other instanceof RNG) && !(actor instanceof RNG)) { return false; }
		}

		// use the supplied collision map:
		return tiles[location.y][location.x] != SOLID;
	}

	public boolean done() {
		// every target must be covered by a Block or a BLU.
		for(int y = 0; y < tiles.length; y++) {
			for(int x = 0; x < tiles[0].length; x++) {
				Location location = new Location(this, x, y);
				if (target(location)) {
					Actor actor = actorAt(location);
					if (actor instanceof Block) { continue; }
					if (actor instanceof BLU)   { continue; }
					return false;
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

	public static class FloatAction extends DestroyAction {
		private final Robots grid;
		public final Location location;
		public final Actor target;
		
		public FloatAction(Robots grid, Actor target) {
			super(grid, target);
			this.grid     = grid;
			this.location = grid.locations.get(target);
			this.target   = target;
		}

		public void apply() {
			super.apply();
			grid.tiles[location.y][location.x] = grid.dark(location) ? DARK_FLOAT : FLOAT;
		}

		public void undo() {
			grid.tiles[location.y][location.x] = grid.dark(location) ? DARK_SLUDGE : SLUDGE;
			super.undo();
		}

		public String toString() {
			return String.format("Float '%s' in Sludge at %s.", target, location);
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

	public class BLU extends GridActor {
		private final int id;

		public BLU(Robots model) {
			super(model);
			id = BLUId++;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Location location : neighbors(ORTHOGONAL)) {
				if (passable(this, location)) {
					ret.add(new MoveAction(model, location, this));
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

	public class RNG extends GridActor {
		private final int id;

		public RNG(Robots model) {
			super(model);
			id = RNGId++;
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			// RNG is solar-powered and becomes inactive in the dark.
			if (dark(location())) { return ret; }

			for(Location location : neighbors(ORTHOGONAL)) {
				if (!passable(this, location)) { continue; }
				ret.add(new MoveAction(model, location, this));
			
				// RNG can drag objects:
				for(Actor other : actorsAt(location())) {
					if ((other instanceof RNG) || !passable(other, location)) { continue; }
					ret.add(new CompositeAction(
						new MoveAction(model, location, this),
						new MoveAction(model, location, other)
					));
				}
			}
			return ret;
		}

		public String toString() {
			return String.format("RNG %d", id);
		}
	}

	public List<Action> cleanup() {
		List<Action> ret = super.cleanup();
		for(Actor actor : actors) {
			Location location = locations.get(actor);
			if (location == null) { continue; }

			if (sludge(location)) {
				if (actor instanceof Block) {
					ret.add(new FloatAction((Robots)model, actor));
				}
				if (actor instanceof BLU) {
					ret.add(new DestroyAction(model, actor));
				}
				if (actor instanceof RNG && dark(location) && actorsAt(location).size() == 1) {
					// If we're dragging something, it takes the plunge first.
					ret.add(new DestroyAction(model, actor));
				}
			}
			else {
				if (actor instanceof RNG && dark(location) && actorsAt(location).size() > 1) {
					// if we fall on something, we blow up.
					ret.add(new DestroyAction(model, actor));
				}
			}
		}
		return ret;
	}
}