package vitro.graph;

import vitro.*;

public abstract class GraphAction implements Action {

	protected final Graph model;

	protected GraphAction(Graph model) {
		this.model = model;
	}
}