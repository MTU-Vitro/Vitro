package assign.search;

import java.util.*;

public class DepthFirstSearch<E> implements Search<E> {
	
	public List<E> search(Domain<E> domain) {
		return (new AbstractSearch<E>()).search(domain, new LinkedList<E>() {
			private static final long serialVersionUID = 1L;
		
			public boolean add(E elem) {
				super.add(0, elem);
				return true;
			}
		});
	}
}
