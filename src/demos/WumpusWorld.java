import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import java.util.*;

public class WumpusWorld extends Graph {

	public class Pit extends Actor {}
	public class Bat extends Actor {}
	public class Wumpus extends Actor {}

	public class Arrow extends Actor {
		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			Graph.Node room = model.getLocation(this);
			Actor wumpus = Groups.firstOfType(Wumpus.class, room.actors);
			Actor hunter = Groups.firstOfType(Hunter.class, room.actors);

			if (wumpus != null) {
				// You bagged a Wumpus!
				// Hee-Hee-Hee... the Wumpus'll getcha next time!
				ret.add(new DestroyAction(model, wumpus, this));
			}
			else if (hunter != null) {
				// Thwok- you were ventilated by an arrow!
				ret.add(new DestroyAction(model, hunter, this));
			}
			else {
				ret.add(new MoveAction(model, Groups.any(room.edges), this));
			}
			return ret;
		}
	}

	public class Hunter extends Actor {
		
		public int arrows = 3;

		public boolean alive() {
			return model.actors.contains(this);
		}

		private Set<Actor> neighbors() {
			return model.getLocation(this).reachableActors(1);
		}

		public boolean flapping() {
			// You hear wings flapping in the distance.
			return Groups.containsType(Bat.class, neighbors());
		}

		public boolean wind() {
			// You feel a draft.
			return Groups.containsType(Pit.class, neighbors());
		}

		public boolean scent() {
			// I smell a Wumpus.
			return Groups.containsType(Wumpus.class, neighbors());
		}

		public boolean whistle() {
			// You hear a faint whistle.
			return Groups.containsType(Arrow.class, neighbors());
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			if (!alive()) { return ret; }
			for(Edge e : model.getLocation(this).edges) {
				ret.add(new WalkAction(model, e, this));
				if (arrows > 0) { ret.add(new ShootAction(model, e.end, this)); }
			}
			return ret;
		}
	}

	public class WalkAction extends MoveAction {
		
		private final Graph.Node randomRoom = Groups.any(model.nodes);

		public WalkAction(Graph model, Graph.Edge e, Hunter h) {
			super(model, e, h);
		}

		public void apply() {
			super.apply();
			Graph.Node room = model.getLocation(actor);
			if (Groups.containsType(Bat.class, room.actors)) {
				// Super-Bats! Elsewheresville for you!
				randomRoom.actors.add(actor);
			}
			else if (Groups.containsType(Pit.class, room.actors)) {
				// You fall a long way down!
				model.actors.remove(actor);
			}
			else if (Groups.containsType(Wumpus.class, room.actors)) {
				// The Wumpus is here. He eats your legs.
				model.actors.remove(actor);
			}
		}
	}

	public class ShootAction extends CreateAction {

		private final Hunter hunter;

		public ShootAction(Graph model, Graph.Node n, Hunter hunter) {
			super(model, n, new Arrow());
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