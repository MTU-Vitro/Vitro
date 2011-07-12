package vitro;

import java.util.*;

public class CompositeAction implements Action {
	public final List<Action> actions;
	
	public CompositeAction(List<Action> actions) {
		this.actions = actions;
	}

	public CompositeAction(Action... actions) {
		this.actions = Arrays.asList(actions);
	}
	
	public void apply() {
		for(int x = 0; x < actions.size(); x++) {
			actions.get(x).apply();
		}
	}
	
	public void undo() {
		for(int x = actions.size() - 1; x >= 0; x--) {
			actions.get(x).undo();
		}
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
