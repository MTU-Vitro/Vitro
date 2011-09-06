package vitro;

import vitro.util.*;
import java.util.*;

/**
* A Controller manages the sequencing of turns in a
* simulation, associates Agents with one or more Actor
* and provides access to historical records of Actions
* and Annotations.
*
* @author John Earnest
**/
public abstract class Controller {
	
	public final Model model;

	protected final Set<Agent> agents = new HashSet<Agent>();
	protected final Map<Class, Agent> classAgents = new HashMap<Class, Agent>();
	protected final Map<Actor, Agent> actorAgents = new HashMap<Actor, Agent>();

	private List<List<Action>>           history   = new ArrayList<List<Action>>();
	private List<Map<Annotation, Agent>> footnotes = new ArrayList<Map<Annotation, Agent>>();
	private int cursor = 0;

	/**
	* Construct a new Controller associated with a given Model.
	*
	* @param model the model which will be driven by this controller.
	**/
	public Controller(Model model) {
		this.model = model;
	}

	/**
	* Associate every Actor that is an instance of a given Class
	* with a specified Agent. Said Agent will be consulted every
	* time the Actor has an opportunity to select an Action.
	*
	* @param c the Class of Actors to which the Agent will apply.
	* @param agent the Agent to associate with Actors.
	**/
	public void bind(Class c, Agent agent) {
		classAgents.put(c, agent);
		agents.add(agent);
	}

	/**
	* Associate a specific Actor with a specified Agent.
	* These bindings will take priority over any Class-based bindings.
	*
	* @param actor the Actor to which the Agent will apply.
	* @param agent the Agent to associate with an Actor.
	**/
	public void bind(Actor actor, Agent agent) {
		actorAgents.put(actor, agent);
		agents.add(agent);
	}

	// As far as I can tell, there is no type-safe way to extract Agents
	// from these maps. Even if I made the map signature like:
	//   Map<Actor, Agent<A extends Actor>>
	// there wouldn't be any way to ensure that the Agent generic type
	// matches the Actor generic type. Having generic agents is
	// desirable because it saves a cast in every implementation.
	// If there's a better way to do this, fix it.
	/**
	* Obtain a reference to the Agent that makes decisions
	* for a specific actor.
	* 
	* @param a an Actor to examine.
	* @return the Agent associated with the Actor or null if no binding exists.
	**/
	@SuppressWarnings("unchecked")
	public <A extends Actor> Agent<A> getAgent(A a) {
		if (actorAgents.containsKey(a))            { return (Agent<A>)actorAgents.get(a); }
		if (classAgents.containsKey(a.getClass())) { return (Agent<A>)classAgents.get(a.getClass()); }
		return null;
	}

	/**
	* Determine the Action a given Actor would choose next.
	* This implementation ensures that the resulting Action is
	* a valid choice given the state of the Model.
	*
	* @param a an Actor for which to decide.
	* @return the selected Action or null.
	**/
	protected <A extends Actor> Action getAction(A a) {
		Set<Action> actions = a.actions();
		Agent<A> agent = getAgent(a);
		if (actions.size() >= 0 && agent != null) {
			Action choice = agent.choose(a, Collections.unmodifiableSet(actions));
			// If the agent returns a malicious Action, using
			// equals() or hashCode() to confirm it was one of the
			// available choices may allow it to slip through
			// the cracks. Thus, we perform reference comparisons:
			if (choice == null) { return null; }
			for(Action action : actions) {
				if (action == choice) { return action; }
			}
			throw new Error("Agent selected an invalid choice.");
		}
		return Groups.first(actions);
	}

	/**
	* @return true if the Model can advance to another state or false if the simulation is 'done'.
	**/
	public boolean hasNext() {
		synchronized(model) {
			return !model.done();
		}
	}

	/**
	* @return true if the Model can be rolled back to a previous state or false if no history exists.
	**/
	public boolean hasPrev() {
		return cursor > 0;
	}

	/**
	* Advance the simulation.
	**/
	public void next() {
		if (!hasNext()) { return; }

		synchronized(model) {		
			// generate a new round:
			if (cursor == history.size()) {

				//long startTime = System.currentTimeMillis();
				List<Action> round = nextRound();
				for(Action action : model.cleanup()) {
					action.apply();
					round.add(action);
				}
				history.add(round);
				//long endTime = System.currentTimeMillis();
				//System.out.format("Elapsed agent time: %d%n", endTime - startTime);

				Map<Annotation, Agent> annotations = new HashMap<Annotation, Agent>();
				for(Agent agent : agents) {
					if (agent instanceof Annotated) {
						for(Annotation a : ((Annotated)agent).annotations()) {
							annotations.put(a, agent);
						}
					}
				}
				footnotes.add(annotations);
				cursor =  history.size();
				return;
			}

			// replay a historical round:
			List<Action> actions = history.get(cursor);
			for(int x = 0; x < actions.size(); x++) {
				actions.get(x).apply();
			}
			cursor++;
		}
	}

	/**
	* Restore the simulation to the previous state.
	**/
	public void prev() {
		if (!hasPrev()) { return; }

		synchronized(model) {
			cursor--;
			List<Action> actions = history.get(cursor);
			for(int x = actions.size() - 1; x >= 0; x--) {
				actions.get(x).undo();
			}
		}
	}

	/**
	* Obtain a collection of Annotations for the current timestep.
	*
	* @return a mapping from every Annotation to the Agent which produced it.
	**/
	public Map<Annotation, Agent> annotations() {
		synchronized(model) {
			if (cursor < 1) { return new HashMap<Annotation, Agent>(); }
			return footnotes.get(cursor-1);
		}
	}

	/**
	* @return a list of every action carried out in the previous timestep.
	**/
	public List<Action> previousActions() {
		if (cursor == 0 || history.size() < 1) {
			return new ArrayList<Action>();
		}
		return history.get(cursor - 1);
	}

	/**
	* Produce a List of Actors which can act during this timestep.
	* If the Model is Factional, Factional Actors will only be listed
	* here if their team() value matches that of the Model.
	*
	* @return a List of Actors which may act.
	**/
	protected List<Actor> actors() {
		if (!(model instanceof Factional)) {
			return new ArrayList<Actor>(model.actors);
		}
		// If we're interacting with a Factional model,
		// only neutral actors and actors from the current
		// team should have a chance to act:
		List<Actor> ret = new ArrayList<Actor>();
		int currentTeam = ((Factional)model).team();
		for(Actor a : model.actors) {
			if (a instanceof Factional) {
				if (((Factional)a).team() != currentTeam) { continue; }
			}
			ret.add(a);
		}
		return ret;
	}
	
	/**
	* @return the current timestep, counting from 0.
	**/
	public int index() {
		return cursor;
	}

	/**
	* This method is used to determine the order in which
	* Actions will be applied. Controller implementations may
	* provide different behaviors here such as evaluating
	* Actions concurrently or sequentially.
	*
	* The List returned by this method is stored for rollback
	* purposes- the Actions themselves should be applied in
	* the body of this method.
	*
	* @return a record of Actions applied as the state is advanced.
	**/
	protected abstract List<Action> nextRound();
}
