package assign.search;

import java.util.*;

public class DepthFirstSearch<E> implements Search<E> {
	
	public List<E> search(Domain<E> domain) {
		return (new AbstractSearch<E>()).search(domain, Collections.asLifoQueue(new ArrayDeque<E>()));
	}
}
