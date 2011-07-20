package demos.wumpus;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;

public class WumpusGrid extends Grid {

	public WumpusGrid(int width, int height) {
		super(width, height);
	}

	public boolean done() {
		return (!Groups.containsType(Wumpus.class, actors)) ||
		       (!Groups.containsType(Hunter.class, actors));
	}

	@Override
	public List<Action> cleanup() {
		List<Action> ret = super.cleanup();

		// move arrows and kill hunters / wumpii
		List<Actor> toKill = new ArrayList<Actor>();
		for(Actor arrow : Groups.ofType(Arrow.class, model.actors)) {
			MoveAction move = new MoveAction(model, ((Arrow)arrow).nextLocation(), arrow);
			ret.add(move);

			move.apply();
			for(Actor actor : actorsAt(model.locations.get(arrow))) {
				if(actor instanceof Hunter || actor instanceof Wumpus) {
					toKill.add(actor);
				}
			}
			move.undo();
		}
		for(Actor dead : toKill) { ret.add(new DestroyAction(this, dead)); }

		return ret;
	}

	// generalized factory method
	private <T extends Actor> T place(T object, int x, int y) {
		locations.put(object, new Location(this, x, y));
		return object;
	}

	public Hunter createHunter(int x, int y) { return place(new Hunter(this), x, y); }
	public Pit    createPit   (int x, int y) { return place(new Pit()       , x, y); }
	public Bat    createBat   (int x, int y) { return place(new Bat()       , x, y); }
	public Wumpus createWumpus(int x, int y) { return place(new Wumpus()    , x, y); }

	public class Hunter extends GridActor {
		private int arrows = 3;

		public Hunter(Grid model) {
			super(model);
		}

		public boolean alive() {
			return model.actors.contains(this);
		}

		public boolean flapping() { return false; }
		public boolean wind()     { return false; }
		public boolean scent()    { return false; }
		public boolean whistle()  { return false; }

		public Set<Action> actions() {
			Set<Action> ret = super.actions();

			if(!alive()) { return ret; }
			for(Location l : neighbors(ORTHOGONAL)) {
				ret.add(new MoveAction(model, l, this));
				if(arrows > 0) { ret.add(new ShootAction(model, l, this)); }
			}

			return ret;
		}
	}
	
	public class Arrow extends GridActor {
		private Location next;

		public Arrow(Grid model, Location next) {
			super(model);
			this.next = next;
		}

		public Location nextLocation() {
			Location ret = next;
			next = Groups.any(neighbors(ORTHOGONAL));
			return ret;
		}
	}
	
	public class Pit    extends Actor {}
	public class Bat    extends Actor {}
	public class Wumpus extends Actor {}
	
	public class ShootAction extends CreateAction {
		private final Hunter hunter;
		
		public ShootAction(Grid model, Location l, Hunter hunter) {
			super(model, model.locations.get(hunter), new Arrow(model, l));
			this.hunter = hunter;
		}
		
		public void apply() {
			super.apply();
			hunter.arrows--;
		}
		
		public void undo() {
			hunter.arrows++;
			super.undo();
		}
	}
}
