package assign.search;

import vitro.util.*;
import java.util.*;

public class AbstractSearch<E> {
	
	public List<E> search(Domain<E> domain, Collection<E> frontier) {
		frontier.add(domain.initial());
		
		Map<E, E> visited = new HashMap<E, E>();
		visited.put(domain.initial(), null);
		
		while(!frontier.isEmpty()) {
			E current = Groups.first(frontier);
			frontier.remove(current);
			
			if(domain.isGoal(current)) {
				List<E> path = new ArrayList<E>();
				
				E state = current;
				while(state != null) {
					path.add(0, state);
					state = visited.get(state);
				}
				
				return path;
			}
			
			for(E next : domain.expand(current)) {
				if(!visited.keySet().contains(next)) {
					frontier.add(next);
					visited.put(next, current);
				}
			}
		}

		return null;
	}
}
