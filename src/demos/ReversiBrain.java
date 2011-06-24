package demos;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

public class ReversiBrain implements Agent<Reversi.Player>, Annotated {

	public Action choose(Reversi.Player actor, Set<Action> options) {
		// If all we can do is pass, go for it.
		if (options.size() == 1) {
			return first(options);
		}

		// Otherwise, greedily select the move that
		// captures the most enemy pieces in the next turn.
		Reversi.Move best = (Reversi.Move)firstOfType(Reversi.Move.class, options);
		for(Action a : options) {
			if (!(a instanceof Reversi.Move)) { continue; }
			Reversi.Move thisMove = (Reversi.Move)a;
			if (thisMove.captured.size() > best.captured.size()) { best = thisMove; }
		}
		return best;
	}

	public Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		
		return ret;
	}
	
}