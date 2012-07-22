package assign.search;

import java.util.*;

public class BreadthFirstSearch<E> extends AbstractSearch<E> {
	
	protected double cost(Domain<E> domain, E head, E tail) {
		return 1.0;
	}
	
	protected double heuristic(E head) {
		return 0.0;
	}

	public List<E> search(Domain<E> domain) {
		return search(domain, new LinkedList<State<E>>());
	}
}
