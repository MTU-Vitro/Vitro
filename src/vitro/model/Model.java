package vitro.model;
import java.util.*;

/**
* A Model is a representation of the state of a simulation.
* All models expose a collection of Actors which can inspect
* and modify the state of the Model through atomic Actions.
* Any other useful state or meaningful constraints can be
* expressed by extending this basic class.
**/
public abstract class Model {

	/**
	* A Collection of the Actors in this Model.
	**/
	public final Set<Actor> actors;

	/**
	* Create a new Model, providing a collection for storing Actors.
	* This collection should generally be mutable.
	**/
	public Model(Set<Actor> actors) {
		this.actors = actors;
	}

	/**
	* Generically determine when this simulation is complete.
	* Simulations that have no meaningful terminal state
	* can return false (the default).
	*
	* @return true if this Model is in a final state.
	**/
	public boolean done() {
		return false;
	}
}