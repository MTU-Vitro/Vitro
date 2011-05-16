import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import java.util.*;

public class VacWorld extends Graph {

	public Dirt    createDirt()    { return new Dirt();    }
	public Scrubby createScrubby() { return new Scrubby(); }

	public boolean clean() {
		return !Groups.containsType(Dirt.class, actors);
	}

	public class Scrubby extends Actor {
		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			for(Edge e : getLocation(this).edges) {
				ret.add(new MoveAction(model, e, this));
			}
			for(Actor a : Groups.ofType(Dirt.class, getLocation(this).actors)) {
				ret.add(new DestroyAction(model, a));
			}
			return ret;
		}
	}

	public class Dirt extends Actor {
		// dirt just sits there.
	}

}