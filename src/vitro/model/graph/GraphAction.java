package vitro.model.graph;

import vitro.model.*;

public abstract class GraphAction implements Action {

	protected final Graph model;

	protected GraphAction(Graph model) {
		this.model = model;
	}
}