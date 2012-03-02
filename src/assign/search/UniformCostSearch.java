package assign.search;

import java.util.*;

public class UniformCostSearch<E> extends AbstractSearch<E> {
	
	protected double cost(Domain<E> domain, E head, E tail) {
		return domain.cost(head, tail);
	}
	
	protected double heuristic(E head) {
		return 0.0;
	}

	public List<E> search(Domain<E> domain) {
		return search(domain, new PriorityQueue<State<E>>());
	}
}
