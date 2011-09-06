package vitro;

/**
* An Annotation meant for associating some textual
* data with an Actor. Like all Annotations, the
* appearance of an ActorAnnotation is up to the
* Model's View, but all default Views should have
* support for this annotation.
*
* @author John Earnest
**/
public class ActorAnnotation implements Annotation {

	public final Actor actor;
	public final String label;

	/**
	* Create a new ActorAnnotation that simply
	* highlights a specific Actor.
	*
	* @param actor Actor to annotate.
	**/
	public ActorAnnotation(Actor actor) {
		this(actor, null);
	}

	/**
	* Create a new ActorAnnotation which applies
	* a label to a specific Actor.
	*
	* @param actor Actor to annotate.
	* @param label a String to associate with actor.
	**/
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