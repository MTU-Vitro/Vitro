package vitro.controller;

import vitro.util.*;
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

	// As far as I can tell, there is no type-safe way to extract Agents
	// from these maps. Even if I made the map signature like:
	//   Map<Actor, Agent<A extends Actor>
	// there wouldn't be any way to ensure that the Agent generic type
	// matches the Actor generic type. Having generic agents is
	// desirable because it saves a cast in every implementation.
	// If there's a better way to do this, fix it.
	@SuppressWarnings("unchecked")
	protected <A extends Actor> Agent<A> getAgent(A a) {
		if (actorAgents.containsKey(a))            { return (Agent<A>)actorAgents.get(a); }
		if (classAgents.containsKey(a.getClass())) { return (Agent<A>)classAgents.get(a.getClass()); }
		return null;
	}

	protected <A extends Actor> Action getAction(A a) {
		Set<Action> actions = a.actions();
		Agent<A> agent = getAgent(a);
		if (actions.size() > 1 && agent != null) {
			return agent.choose(a, actions);
		}
		return Groups.first(actions);
	}

	public abstract void next();
	public abstract void prev();

}