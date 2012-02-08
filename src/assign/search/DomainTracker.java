package demos.search;

import java.util.*;

public class DomainTracker<E> implements Domain<E> {
	protected final Domain<E> domain;
	
	private int count;
	private Map<E, Integer> expansions;

	public DomainTracker(Domain<E> domain) {
		this.domain = domain;
		
		count = 0;
		expansions = new HashMap<E, Integer>();
	}
	
	public E initial() {
		return domain.initial();
	}
	
	public boolean isGoal(E e) {
		return domain.isGoal(e);
	}
	
	public Set<E> expand(E e) {
		count += 1;
		expansions.put(e, count);

		return domain.expand(e);
	}
	
	public Map<E, Integer> expandOrder() {
		return Collections.unmodifiableMap(expansions);
	}
}
