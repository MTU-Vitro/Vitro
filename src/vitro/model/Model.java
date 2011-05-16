package vitro.model;
import java.util.*;

public abstract class Model {
	public final Set<Actor> actors;

	public Model(Set<Actor> actors) {
		this.actors = actors;
	}

	public boolean done() {
		return false;
	}
}