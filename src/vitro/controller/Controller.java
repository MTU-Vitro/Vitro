package vitro.controller;

import vitro.model.*;
import java.util.*;

public abstract class Controller {
	
	public final Model model;

	protected final Map<Class, Agent> classAgents = new HashMap<Class, Agent>();
	protected final Map<Actor, Agent> actorAgents = new HashMap<Actor, Agent>();

	public Controller(Model model) {
		this.model = model;
	}

	public void bind(Class c, Agent agent) {
		classAgents.put(c, agent);
	}

	public void bind(Actor actor, Agent agent) {
		actorAgents.put(actor, agent);
	}

	public abstract void next();
	public abstract void prev();

}