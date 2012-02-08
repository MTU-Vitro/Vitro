package demos.search;

import java.util.*;

public class UniformCostSearch<E> implements Search<E> {
	public final CostFunction<E> costFunction;

	public UniformCostSearch(CostFunction<E> costFunction) {
		this.costFunction = costFunction;
	}

	public List<E> search(Domain<E> domain) {
		Comparator<E> comparator = new Comparator<E>() {
			public int compare(E e1, E e2) {
				Double value1 = costFunction.value(e1);
				Double value2 = costFunction.value(e2);
				
				return value1.compareTo(value2);
			}
		};
	
		return (new AbstractSearch<E>()).search(domain, new PriorityQueue<E>(100, comparator));
	}
}
