package assign.search;

import java.util.*;

public class AStarSearch<E> implements Search<E> {
	public final CostFunction<E> costFunction;
	public final Heuristic<E>    heuristic;
	
	public AStarSearch(CostFunction<E> costFunction, Heuristic<E> heuristic) {
		this.costFunction = costFunction;
		this.heuristic    = heuristic;
	}
	
	public List<E> search(Domain<E> domain) {
		Comparator<E> comparator = new Comparator<E>() {
			public int compare(E e1, E e2) {
				Double value1 = costFunction.value(e1) + heuristic.value(e1);
				Double value2 = costFunction.value(e1) + heuristic.value(e2);
				
				return value1.compareTo(value2);
			}
		};
	
		return (new AbstractSearch<E>()).search(domain, new PriorityQueue<E>(100, comparator));
	}
}
