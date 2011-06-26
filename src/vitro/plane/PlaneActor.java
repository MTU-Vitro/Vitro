package vitro.plane;

import vitro.*;
import java.util.*;


public class PlaneActor extends Actor {

	protected final Plane model;
	
	public PlaneActor(Plane model) {
		this.model = model;
	}
	
	public Frame reference() {
		return model.frames.get(this);
	}
	
	void moves() {}
	void create() {}
	void destroy() {}
}
