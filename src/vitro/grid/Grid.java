package vitro.grid;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Grid extends Model {

	protected final Grid model;

	public final int width;
	public final int height;
	public final Map<Actor, Location> locations;

	public static final int[][] ORTHOGONAL = {{1, 0},{-1, 0},{0, 1},{0, -1}};
	public static final int[][] DIAGONAL   = {{1, 1},{-1, 1},{1,-1},{-1,-1}};
	public static final int[][] ADJACENT   = {{1, 0},{-1, 0},{0, 1},{0, -1},{1, 1},{-1, 1},{1,-1},{-1,-1}};

	private final CollectionObserver<Actor>                      actorObserver    = new ActorObserver();
	private final CollectionObserver<Map.Entry<Actor, Location>> locationObserver = new LocationObserver();

	public Grid(int width, int height) {
		super(new ObservableSet<Actor>());
		locations = new ObservableMap<Actor, Location>();

		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableMap<Actor, Location>)locations).addObserver(locationObserver);

		this.width  = width;
		this.height = height;
		model = this;
	}

	public Set<Actor> actorsAt(Location location) {
		Set<Actor> ret = new HashSet<Actor>();
		for(Map.Entry<Actor, Location> e : locations.entrySet()) {
			if (location.equals(e.getValue())) { ret.add(e.getKey()); }
		}
		return ret;
	}

	// by overriding this method, users
	// can easily add their own passability
	// system of arbitrary complexity.
	protected boolean passable(Actor actor, Location location) {
		return true;
	}

	public Set<Location> passable(Actor actor, Set<Location> locations) {
		Set<Location> ret = new HashSet<Location>();
		for(Location location : locations) {
			if (passable(actor, location)) { ret.add(location); }
		}
		return ret;
	}

	private class ActorObserver implements CollectionObserver<Actor> {		
		public void added(ObservableCollection sender, Actor e) {
			// If an actor is added 'Raw' we don't
			// need to assign a default location.
		}
		
		public void removed(ObservableCollection sender, Actor e) {
			((ObservableMap<Actor,Location>)locations).store().remove(e);
		}
	}

	private class LocationObserver implements CollectionObserver<Map.Entry<Actor, Location>> {
		public void added(ObservableCollection sender, Map.Entry<Actor, Location> e) {
			((ObservableSet<Actor>)actors).store().add(e.getKey());
		}

		public void removed(ObservableCollection sender, Map.Entry<Actor, Location> e) {
			((ObservableSet<Actor>)actors).store().remove(e.getKey());
		}
	}
}