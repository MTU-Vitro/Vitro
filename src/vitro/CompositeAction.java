package vitro;

import java.util.*;

/**
* A CompositeAction offers an easy way to carry
* out several existing Actions in sequence as
* an atomic operation.
*
* Component Actions will be applied in the order
* given, and rolled back in reverse order.
*
* @author John Earnest
**/
public class CompositeAction implements Action {
	public final List<Action> actions;
	
	/**
	* Create a new CompositeAction from a list
	* of Actions.
	*
	* @param actions the List of Actions to perform.
	**/
	public CompositeAction(List<Action> actions) {
		this.actions = actions;
	}

	/**
	* An alternate vararg-based constructor.
	*
	* @param actions a sequence of Actions to perform.
	**/
	public CompositeAction(Action... actions) {
		this.actions = Arrays.asList(actions);
	}
	
	/**
	* {@inheritDoc}
	**/
	public void apply() {
		for(int x = 0; x < actions.size(); x++) {
			actions.get(x).apply();
		}
	}

	/**
	* {@inheritDoc}
	**/
	public void undo() {
		for(int x = actions.size() - 1; x >= 0; x--) {
			actions.get(x).undo();
		}
	}

	/**
	* {@inheritDoc}
	**/
	@Override
	public int hashCode() {
		return actions.hashCode();
	}

	/**
	* {@inheritDoc}
	**/
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof CompositeAction)) { return false; }
		CompositeAction other = (CompositeAction)o;
		return other.actions.equals(actions);
	}
	
	/**
	* {@inheritDoc}
	**/
	@Override
	public String toString() {
		return actions.toString();
	}
}
