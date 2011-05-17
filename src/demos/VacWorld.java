import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import java.util.*;

public class VacWorld extends Graph {

	public Dirt    createDirt()    { return new Dirt();        }
	public Scrubby createScrubby() { return new Scrubby(this); }

	public boolean done() {
		return !Groups.containsType(Dirt.class, actors);
	}

	public class Scrubby extends GraphActor {

		public Scrubby(Graph model) {
			super(model);
		}

		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Edge e : location().edges) {
				ret.add(new MoveAction(model, e, this));
			}
			for(Actor a : Groups.ofType(Dirt.class, location().actors)) {
				ret.add(new DestroyAction(model, a));
			}
			return ret;
		}
	}

	public class Dirt extends Actor {
		// dirt just sits there.
	}

}