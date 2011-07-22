package demos.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import java.awt.Color;


public class SearchAgent implements Agent<Search.SearchActor>, Annotated {

	public enum Type { BREADTH, DEPTH, ASTAR; }
	
	public final Type type;

	public final Map<Actor, List<Location>>         paths      = new HashMap<Actor, List<Location>>();
	
	public final Map<Actor, Map<Location, Integer>> expansions = new HashMap<Actor, Map<Location, Integer>>();

	//
	private final Map<Actor, List<Location>> pathing = new HashMap<Actor, List<Location>>();

	public SearchAgent(Type type) {
		this.type = type;
	}

	public final Action choose(Search.SearchActor actor, Set<Action> options) {
		if(!paths.keySet().contains(actor)) {
			Search.Domain  domain = actor.domain();
			
			List<Location> path = null;
			switch(type) {
				case BREADTH : path = (new Pathing()).pathBFS(domain);   break;
				case DEPTH   : path = (new Pathing()).pathDFS(domain);   break;
				case ASTAR   : path = (new Pathing()).pathAStar(domain); break;
			}
			
			paths.put(actor, path);
			expansions.put(actor, domain.expandOrder());
			
			List<Location> remaining = new ArrayList<Location>(path);
			remaining.remove(0);
			pathing.put(actor, remaining);
			
			return null;
		}

		List<Location> remaining = pathing.get(actor);
		if(remaining == null || remaining.isEmpty()) { return null; }
		
		Location next = remaining.remove(0);
		return actor.move(next, options);
	}
	
	public final Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		for(Actor actor : expansions.keySet()) {
			ret.add(new GridAnnotation(expansions.get(actor), Color.WHITE, Color.GRAY));
		}
		return ret;
	}
}
