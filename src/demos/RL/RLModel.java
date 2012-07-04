package demos.RL;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

public class RLModel extends Grid {

	private final RLActor actor = new RLActor(this);
	private final int[][] type;
	private final int[][] reward;

	private final double forward;
	private final double left;
	private final double right;
	
	private boolean terminated;

	public RLModel(double n, double e, double s, double w, int[][] type, int[][] reward) {
		super(type[0].length, type.length);
		this.type   = type;
		this.reward = reward;
		if (type[0].length != reward[0].length ||
		    type.length    != reward.length) {
			throw new IllegalArgumentException("Type and Reward matrices must have the same size!");
		}

		double sum = n + e + s + w;
		this.forward = n / sum;
		this.left    = w / sum;
		this.right   = e / sum;
		
		terminated = false;
	}

	public RLActor actor() {
		return actor;
	}

	public void startPosition(int x, int y) {
		put(actor, x, y);
	}

	public boolean goal(int x, int y) {
		return type[y][x] == 2;
	}

	public int reward(int x, int y) {
		return reward[y][x];
	}

	public boolean goal(Location l) {
		return goal(l.x, l.y);
	}

	public int reward(Location l) {
		return reward(l.x, l.y);
	}

	public boolean passable(Actor actor, Location location) {
		if (!location.valid()) { return false; }
		return type[location.y][location.x] != 0;
	}
	
	public void terminated(boolean terminate) {
		terminated = terminate;
	}
	
	public boolean done() {
		return terminated;
		//Location l = locations.get(actor);
		//if (type[l.y][l.x] == 2) { return true; }
		//return false;
	}

	private static int[][] deltas = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
	private Location inDir(Location src, int dir) {
		int[] d = deltas[dir % 4];
		return src.add(d[0], d[1]);
	}

	public int direction(Location src, Location dst) {
		if (src.x     == dst.x && src.y - 1 == dst.y) { return 0; }
		if (src.x + 1 == dst.x && src.y     == dst.y) { return 2; }
		if (src.x     == dst.x && src.y + 1 == dst.y) { return 4; }
		if (src.x - 1 == dst.x && src.y     == dst.y) { return 6; }
		throw new Error("Move must be orthogonal!");
	}

	public Location probMove(Location src, Location dst) {
		int dir = direction(src, dst) / 2;
		Location lRight = inDir(src, dir + 1);
		Location lBack  = inDir(src, dir + 2);
		Location lLeft  = inDir(src, dir + 3);
		double r = Math.random();

		// when blocked, stay in the same place:
		if (r < forward) { return passable(actor, dst)    ? dst    : src; } r -= forward;
		if (r < left   ) { return passable(actor, lLeft)  ? lLeft  : src; } r -= left;
		if (r < right  ) { return passable(actor, lRight) ? lRight : src; }
		return                    passable(actor, lBack)  ? lBack  : src;

		/*
		// when blocked, try another direction falling through to the original direction:
		if (r < forward                         ) { return dst;    } r -= forward;
		if (r < left  && passable(actor, lLeft) ) { return lLeft;  } r -= left;
		if (r < right && passable(actor, lRight)) { return lRight; }
		if (             passable(actor, lBack) ) { return lBack;  }
		return dst;
		*/
	}

	public List<Action> cleanup() {
		return Collections.singletonList((Action)new Reward(this));
	}
}

class RLMove implements Action {
	public  final RLModel  model;
	public  final Location start;
	public  final Location end;
	private final Location actualDest;

	public RLMove(RLModel model, Location destination) {
		this.model  = model;
		this.start  = model.actor().location();
		this.end    = destination;
		this.actualDest = model.probMove(this.start, this.end);
	}

	public void apply() {
		model.locations.put(model.actor(), actualDest);
	}

	public void undo() {
		model.locations.put(model.actor(), start);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RLMove)) { return false; }
		RLMove other = (RLMove)o;
		return start.equals(other.start) &&
		         end.equals(other.end  );
	}

	@Override
	public int hashCode() {
		return start.hashCode() ^
		         end.hashCode();
	}
}

class RLTerminate implements Action {
	public final RLModel model;
	
	public RLTerminate(RLModel model) {
		this.model = model;
	}
	
	public void apply() {
		model.terminated(true);
	}
	
	public void undo() {
		model.terminated(false);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RLTerminate)) { return false; }
		return true;
	}

	@Override
	public int hashCode() {
		return 1;
	}
}

class Reward implements Action {
	public final RLActor actor;
	public final int reward;

	public Reward(RLModel model) {
		this.actor  = model.actor();
		this.reward = model.reward(actor.location());
	}

	public void apply() {
		actor.reward += reward;
	}

	public void undo() {
		actor.reward -= reward;
	}
}

class RLActor extends GridActor {
	public int reward = 0;

	public RLActor(RLModel model) {
		super(model);
	}

	public Set<Action> actions() {
		Set<Action> ret = super.actions();
		
		if (((RLModel)model).goal(location())) {
			ret.add(new RLTerminate((RLModel)model));
			return ret;
		}
		
		for(Location location : passableNeighbors(ORTHOGONAL)) {
			ret.add(new RLMove((RLModel)model, location));
		}
		return ret;
	}
}