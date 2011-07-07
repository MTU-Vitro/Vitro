package vitro;

import java.util.*;

public class CompositeAction implements Action {
	public final List<Action> actions;
	
	public CompositeAction(List<Action> actions) {
		this.actions = actions;
	}
	
	public void apply() {
		for(Action action : actions) { action.apply(); }
	}
	
	public void undo() {
		for(Action action : actions) { action.undo();  }
	}
	
	@Override
	public int hashCode() {
		return actions.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof CompositeAction)) { return false; }
		CompositeAction other = (CompositeAction)o;
		return other.actions.equals(actions);
	}
	
	@Override
	public String toString() {
		return actions.toString();
	}
}
