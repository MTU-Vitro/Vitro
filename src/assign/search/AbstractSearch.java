package assign.search;

import vitro.util.*;
import java.util.*;

public abstract class AbstractSearch<E> implements Search<E> {
	
	protected abstract double cost(Domain<E> domain, E head, E tail);
	protected abstract double heuristic(E head);
	
	protected List<E> search(Domain<E> domain, Queue<State<E>> frontier) {
		Map<E, E> visited = new HashMap<E, E>();
		
		// setup the initial state
		E initial = domain.initial();
		frontier.add(new State<E>(
			initial, 
			0.0, 
			heuristic(initial)
		));
		visited.put(initial, null);

		//
		while(!frontier.isEmpty()) {
			State<E> current = frontier.remove();

			// construct the path and return if the goal has been reached
			if(domain.isGoal(current.element)) {
				List<E> path = new ArrayList<E>();
				
				E node = current.element;
				while(node != null) {
					path.add(0, node);
					node = visited.get(node);
				}
				
				return path;
			}

			// otherwise we expand and add the successors to the queue
			for(E next : domain.expand(current.element)) {
				if(!visited.keySet().contains(next)) {
					frontier.add(new State<E>(
						next, 
						current.cost + cost(domain, current.element, next),
						heuristic(next)
					));
					visited.put(next, current.element);
				}
			}
		}

		return null;
	}
	
	protected final class State<E> implements Comparable<State<E>> {
		public final E      element;
		public final double cost;
		public final double heuristic;
		
		public State(E element, double cost, double heuristic) {
			this.element   = element;
			this.cost      = cost;
			this.heuristic = heuristic;
		}
		
		public final int compareTo(State<E> o) {
			return Double.compare(cost + heuristic, o.cost + o.heuristic);
		}
		
		public final boolean equals(Object o) {
			return element.equals(o);
		}
		
		public final int hashCode() {
			return element.hashCode();
		}
	}
}
