import java.util.*;

public abstract class Model {
	public final Set<Actor> actors;

	public Model(Set<Actor> actors) {
		this.actors = actors;
	}
}