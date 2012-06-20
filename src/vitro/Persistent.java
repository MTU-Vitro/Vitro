package vitro;

/**
* Persistent is a mixin interface which allows
* Models and Agents used with a LoopController
* to preserve data between trials.
*
* @author John Earnest
**/

public interface Persistent {

	/**
	* Freeze is used to preserve the state
	* from one trial to the next. Freeze will
	* be called on Agents and Models even at the
	* end of the last trial given by a LoopController,
	* which also makes this method a useful place
	* for evaluating the result of a simulation run.
	*
	* @param m a reference to the current Model.
	* @return data to preserve across trials.
	**/
	public Object freeze(Model m);

	/**
	* Restore the state of this object from
	* a previous trial.
	*
	* @param o data preserved across trials.
	**/
	public void thaw(Object o);

}