package vitro;

import java.io.*;
import java.util.*;

public class LoopController extends Controller {

	private Controller instance;
	private final Controller basis;
	private int loopCount;

	public LoopController(Controller basis, int loopCount) {
		super(null);
		this.basis     = basis;
		this.loopCount = loopCount;
	}

	@Override
	public Model model() {
		return instance().model();
	}

	// lazily instantiate the clone Controller:
	private Controller instance() {
		synchronized(this) {
			if (instance == null) {
				// horrifically abuse the serialization framework to
				// perform a deep copy of the basis Controller:
				try {
					ByteArrayOutputStream outa = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(outa);
					out.writeObject(basis);
					out.flush();
					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outa.toByteArray()));
					instance = (Controller)in.readObject();
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
					throw new Error("Failed to deep-copy basis Controller.");
				}
				catch(IOException e) {
					e.printStackTrace();
					throw new Error("Failed to deep-copy basis Controller.");
				}
			}
		}
		return instance;
	}

	// Some methods are thin wrappers that forward
	// directly to the underlying Controller:
	@Override
	public void bind(Class c, Agent agent) {
		basis.bind(c, agent);
		instance = null;
	}
	@Override
	public void bind(Actor actor, Agent agent) {
		basis.bind(actor, agent);
		instance = null;
	}
	@Override
	public void prev() {
		synchronized(this) {
			instance().prev();
		}
	}
	@Override
	public Map<Annotation, Agent> annotations() {
		synchronized(this) {
			return instance().annotations();
		}
	}

	@Override
	public <A extends Actor> Agent<A> getAgent(A a) { return basis.getAgent(a); }
	@Override
	public int getId(Object o) { return instance().getId(o); }
	@Override                                       
	public int index() { return instance().index(); }
	@Override                               
	public List<Action> previousActions() { return instance().previousActions(); }
	@Override
	public boolean hasPrev() { return instance().hasPrev(); }
	protected List<Action> nextRound() { return instance().nextRound(); }


	@Override
	public boolean hasNext() {
		synchronized(this) {
			if (instance().hasNext()) { return true; }
			return loopCount > 0;
		}
	}

	private void freeze(Map<Integer, Object> frozen, Object o) {
		int id = getId(o);
		if (o instanceof Persistent) {
			frozen.put(id, ((Persistent)o).freeze(model()));
		}
	}

	private void thaw(Map<Integer, Object> frozen, Object o) {
		int id = getId(o);
		if (o instanceof Persistent && frozen.containsKey(id)) {
			((Persistent)o).thaw(frozen.get(id));
		}
	}

	@Override
	public synchronized void next() {
		synchronized(this) {
			if (instance().hasNext()) {
				instance().next();
				return;
			}
			if (loopCount == 0) {
				return;
			}

			// freeze all Persistent data.
			// always freeze data, even if it's the last
			// iteration. This allows Agents to treat 'freeze'
			// as an opportunity to inspect world state and analyze it:
			Map<Integer, Object> frozen = new HashMap<Integer, Object>();
			freeze(frozen, instance().model());
			for(Agent a : instance().agents) {
				freeze(frozen, a);
			}

			if (loopCount > 1) {
				instance = null;

				// restore all Persistent data
				thaw(frozen, instance().model());
				for(Agent a : instance().agents) {
					thaw(frozen, a);
				}
			}

			loopCount--;
		}
	}
}