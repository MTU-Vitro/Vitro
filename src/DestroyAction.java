import java.util.*;

public class DestroyAction extends GraphAction {

	protected final Map<Actor, Graph.Node> actors = new HashMap<Actor, Graph.Node>();

	public DestroyAction(Graph model, Actor... targets) {
		super(model);
		for(Actor a : targets) {
			actors.put(a, model.getLocation(a));
		}
	}

	public void apply() {
		for(Map.Entry<Actor, Graph.Node> e : actors.entrySet()) {
			e.getValue().actors.remove(e.getKey());
		}
	}

	public void undo() {
		for(Map.Entry<Actor, Graph.Node> e : actors.entrySet()) {
			e.getValue().actors.add(e.getKey());
		}
	}

	@Override
	public int hashCode() {
		return actors.hashCode() ^
		        model.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyAction)) { return false; }
		DestroyAction other = (DestroyAction)o;
		return (other.actors.equals(actors)) &&
		       (other.model == this.model  );
	}

	@Override
	public String toString() {
		return String.format("Destroy actors: %s", actors.keySet());
	}
}