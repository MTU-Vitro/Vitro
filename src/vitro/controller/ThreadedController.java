package vitro.controller;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class ThreadedController extends Controller {

	private final long timeout;

	public ThreadedController(Model model, long timeout) {
		super(model);
		this.timeout = timeout;
	}

	@SuppressWarnings("deprecation")
	public List<Action> nextRound() {
		List<Decider> threads = new ArrayList<Decider>();
		for(Actor a : model.actors) {
			Decider d = new Decider(a);
			threads.add(d);
			d.start();
		}

		List<Action> actions = new ArrayList<Action>();
		for(Decider d : threads) {
			// Keep in mind that enforcing the timeout on a
			// per-thread model means that an agent timing
			// out (or a large number of agents)
			// could allow other agents extra time.
			// As written, the timeout is the maximum
			// amount of time a given Agent should *expect*
			// for coming to a decision.
			try { d.join(timeout); }
			catch(InterruptedException ie) {}

			// Unfortunately, the InterruptedException
			// mechanism is not sufficient for terminating
			// a runaway Agent thread unless we make some
			// complex demands on implementers.
			// Despite it being a Bad Idea(tm) in general,
			// Thread.stop() can be used safely here.
			if (d.isAlive()) { d.stop(); }

			if (d.action != null) {
				actions.add(d.action);
			}
		}

		for(Action a : actions) {
			a.apply();
		}
		return actions;
	}

	private class Decider extends Thread {
		private Actor actor;
		public volatile Action action = null;

		public Decider(Actor actor) {
			this.actor = actor;
		}

		public void run() {
			Set<Action> actions = Collections.unmodifiableSet(actor.actions());
			Action choice = getAgent(actor).choose(actor, actions);
			// If the agent returns a malicious Action, using
			// equals() or hashCode() to confirm it was one of the
			// available choices may allow it to slip through
			// the cracks. Thus, we perform reference comparisons:
			for(Action a : actions) {
				if (a == choice) { return action = a; }
			}
			throw new Error("Agent selected an invalid choice.");
		}
	}
}