package demos.search;

import java.util.*;

public class BreadthFirstSearch<E> implements Search<E> {
	
	public List<E> search(Domain<E> domain) {
		return (new AbstractSearch<E>()).search(domain, new LinkedList<E>());
	}
}
