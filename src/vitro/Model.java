package vitro;
import java.io.Serializable;
import java.util.*;

/**
* A Model is a representation of the state of a simulation.
* All models expose a collection of Actors which can inspect
* and modify the state of the Model through atomic Actions.
* Any other useful state or meaningful constraints can be
* expressed by extending this basic class.
*
* @author John Earnest
**/
public abstract class Model implements Serializable {

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
	
	/**
	* This method will be called by the Controller after
	* all Agents have made their decisions for their
	* respective Actors. The returned list of Actions will
	* be applied in the order given.
	*
	* @return cleanup Actions to perform.
	**/
	public List<Action> cleanup() {
		return new LinkedList<Action>();
	}
}
