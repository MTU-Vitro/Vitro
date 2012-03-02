package assign.search;

import java.util.*;

public class DepthFirstSearch<E> extends AbstractSearch<E> {
	
	protected double cost(Domain<E> domain, E head, E tail) {
		return 1.0;
	}
	
	protected double heuristic(E head) {
		return 0.0;
	}

	public List<E> search(Domain<E> domain) {
		Deque<State<E>> queue = new ArrayDeque<State<E>>();
		return search(domain, Collections.asLifoQueue(queue));
	}
}
