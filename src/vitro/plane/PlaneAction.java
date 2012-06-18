package vitro.plane;

import vitro.*;

public abstract class PlaneAction implements Action {

	protected final Plane model;
	
	protected PlaneAction(Plane model) {
		this.model = model;
	}
}
