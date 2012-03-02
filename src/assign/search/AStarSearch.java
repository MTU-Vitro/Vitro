package assign.search;

import java.util.*;

public class AStarSearch<E> extends AbstractSearch<E> {
	public final Heuristic<E> heuristic;
	
	public AStarSearch(Heuristic<E> heuristic) {
		this.heuristic = heuristic;
	}
	
	protected double cost(Domain<E> domain, E head, E tail) {
		return domain.cost(head, tail);
	}
	
	protected double heuristic(E head) {
		return heuristic.cost(head);
	}

	public List<E> search(Domain<E> domain) {
		return search(domain, new PriorityQueue<State<E>>());
	}
}
