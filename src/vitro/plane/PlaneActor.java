package vitro.plane;

import vitro.*;
import java.util.*;


public class PlaneActor extends Actor {

	protected final Plane model;
	
	public PlaneActor(Plane model) {
		this.model = model;
	}
	
	public Position position() {
		return model.positions.get(this);
	}
	
	void moves() {}
	void create() {}
	void destroy() {}
}
