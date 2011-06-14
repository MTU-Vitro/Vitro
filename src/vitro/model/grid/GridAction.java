package vitro.grid;

import vitro.*;

public abstract class GridAction implements Action {

	protected final Grid model;

	protected GridAction(Grid model) {
		this.model = model;
	}
}