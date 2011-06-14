package vitro;

public class ActorAnnotation implements Annotation {

	public final Actor actor;
	public final String label;

	public ActorAnnotation(Actor actor) {
		this(actor, null);
	}

	public ActorAnnotation(Actor actor, String label) {
		this.actor = actor;
		this.label = label;
	}

	@Override
	public String toString() {
		if (label != null) { return label; }
		return super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ActorAnnotation)) { return false; }
		ActorAnnotation other = (ActorAnnotation)o;
		if (!actor.equals(other.actor)) { return false; }
		if (label == null) { return other.label == null; }
		else { return label.equals(other.label); }
	}

	@Override
	public int hashCode() {
		if (label == null) { return actor.hashCode(); }
		return actor.hashCode() ^ label.hashCode();
	}

}