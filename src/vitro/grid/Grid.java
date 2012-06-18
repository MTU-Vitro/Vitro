package vitro.grid;

import vitro.*;
import vitro.util.*;
import java.util.*;

/**
* Grid is a Model implementation representing space as
* a two-dimensional rectangular grid. Multiple Actors
* can exist in the same grid cell at once by default,
* but this behavior can be modified by overriding
* the passable() predicates.
*
* @author John Earnest
**/
public class Grid extends Model {

	/**
	* A reference to the current Model.
	**/
	protected final Grid model;

	/**
	* The width of the Grid in cells.
	**/
	public final int width;

	/**
	* The height of the Grid in cells.
	**/
	public final int height;

	/**
	* A mapping from Actors in this Model to the Grid cell in which they are currently located.
	**/
	public final Map<Actor, Location> locations;

	// internal cache of actor locations:
	private final Map<Location, List<Actor>> actorLocations;

	/**
	* A set of x/y deltas for vertically or horizontally adjacent cells.
	**/
	public static final int[][] ORTHOGONAL = {{1, 0},{-1, 0},{0, 1},{0, -1}};
	/**
	* A set of x/y deltas for diagonally adjacent cells.
	**/
	public static final int[][] DIAGONAL   = {{1, 1},{-1, 1},{1,-1},{-1,-1}};
	/**
	* A set of x/y deltas for vertically, horizontally and diagonally adjacent cells.
	**/
	public static final int[][] ADJACENT   = {{1, 0},{-1, 0},{0, 1},{0, -1},{1, 1},{-1, 1},{1,-1},{-1,-1}};

	private final CollectionObserver<Actor>                      actorObserver    = new ActorObserver();
	private final CollectionObserver<Map.Entry<Actor, Location>> locationObserver = new LocationObserver();

	/**
	* Create a new Grid with a specified size.
	*
	* @param width the width of the Grid in cells.
	* @param height the height of the Grid in cells.
	**/
	public Grid(int width, int height) {
		super(new ObservableSet<Actor>());
		locations = new ObservableMap<Actor, Location>();
		actorLocations = new HashMap<Location, List<Actor>>();

		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableMap<Actor, Location>)locations).addObserver(locationObserver);

		this.width  = width;
		this.height = height;
		model = this;
	}

	/**
	* Place a new Actor at a specified Location.
	* Equivalent to this.locations.put(actor, new Location(this, x, y))
	*
	* @param actor the Actor to place in the grid.
	* @param x the column in which to place the Actor.
	* @param y the row in which to place the Actor.
	**/
	public void put(Actor actor, int x, int y) {
		locations.put(actor, new Location(this, x, y));
	}

	/**
	* Obtain references to Location objects representing neighboring cells.
	* The array of deltas provided is a list of 2-length arrays representing
	* an x-offset followed by a y-offset. See the constant arrays ORTHOGONAL, DIAGONAL and ADJACENT.
	*
	* @param location the origin Location.
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of neighboring Locations.
	**/
	public Set<Location> neighbors(Location location, int[][] deltas) {
		Set<Location> ret = new HashSet<Location>();
		for(int[] d : deltas) {
			int nx = location.x + d[0];
			int ny = location.y + d[1];
			if (nx >= 0 && nx < model.width && ny >= 0 && ny < model.height) {
				ret.add(new Location(model, nx, ny));
			}
		}
		return ret;
	}

	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to a specified Actor. In the case of deltas with a magnitude
	* greater than one, note that this method only checks passability of destinations
	* and does not check that there is an intervening path of passable adjacent cells.
	*
	* @param actor the actor to consider moving
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(Actor actor, int[][] deltas) {
		return passable(actor, neighbors(locations.get(actor), deltas));
	}
	
	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to any Actor.
	*
	* @param location the origin Location.
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(Location location, int[][] deltas) {
		return passable(null, neighbors(location, deltas));
	}

	/**
	* Find the first Actor at a given Location.
	* If multiple Actors exist at a given Location the
	* results of this method are not guaranteed to be consistent.
	*
	* @param location the Location to check for Actors.
	* @return the Actor at the Location or null if no Actors exist.
	**/
	public Actor actorAt(Location location) {
		List<Actor> retList = actorLocations.get(location);
		if (retList == null)    { return null; }
		if (retList.size() < 1) { return null; }
		return retList.get(0);
	}

	/**
	* Find all Actors at a given Location.
	*
	* @param location the Location to check for Actors.
	* @return a set of Actors at the Location.
	**/
	public Set<Actor> actorsAt(Location location) {
		List<Actor> retList = actorLocations.get(location);
		if (retList == null) { return Collections.emptySet(); }
		// we must build a new collection to return to
		// avoid leaking our internal caches:
		return new HashSet<Actor>(retList);
	}

	/**
	* Find all Actors in a group of Locations.
	*
	* @param locations a set of Locations to check for Actors.
	* @return a set of Actors at the Locations.
	**/
	public Set<Actor> actorsAt(Set<Location> locations) {
		Set<Actor> ret = new HashSet<Actor>();
		for(Location location : locations) {
			List<Actor> actors = actorLocations.get(location);
			if (actors != null) { ret.addAll(actors); }
		}
		return ret;
	}

	/**
	* Obtain references to all cells on the Grid.
	*
	* @return a set of Locations representing every cell on the Grid.
	**/
	public Set<Location> allCells() {
		Set<Location> ret = new HashSet<Location>();
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				ret.add(new Location(this, x, y));
			}
		}
		return ret;
	}

	/**
	* Obtain references to all cells on the Grid which contain no Actors.
	*
	* @return a set of Locations representing empty cells on the Grid.
	**/
	public Set<Location> emptyCells() {
		Set<Location> ret = allCells();
		ret.removeAll(locations.values());
		return ret;
	}

	/**
	* Check the passability of a given Location with respect
	* to a given Actor. This method is leveraged by many
	* of the convenience methods in Grid and by default
	* always returns true. By overriding this method,
	* subclasses can easily add their own passability
	* system of arbitrary complexity.
	*
	* @param actor the Actor to consider.
	* @param location the Location to consider.
	* @return true if the Actor can move to this Location.
	**/
	public boolean passable(Actor actor, Location location) {
		return true;
	}

	/**
	* Find the Locations that a given Actor could move to.
	* Makes use of passable(actor, location).
	*
	* @param actor the Actor to consider.
	* @param locations a set of Locations to consider.
	* @return a set of passable Locations.
	**/
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
			Location location = locations.get(e);
			if (location != null) {
				actorLocations.get(location).remove(e);
			}
			((ObservableMap<Actor,Location>)locations).store().remove(e);
		}
	}

	private class LocationObserver implements CollectionObserver<Map.Entry<Actor, Location>> {
		public void added(ObservableCollection sender, Map.Entry<Actor, Location> e) {
			((ObservableSet<Actor>)actors).store().add(e.getKey());

			List<Actor> a = actorLocations.get(e.getValue());
			if (a == null) {
				a = new ArrayList<Actor>();
				actorLocations.put(e.getValue(), a);
			}
			a.add(e.getKey());
		}

		public void removed(ObservableCollection sender, Map.Entry<Actor, Location> e) {
			((ObservableSet<Actor>)actors).store().remove(e.getKey());

			List<Actor> a = actorLocations.get(e.getValue());
			if (a != null) {
				a.remove(e.getKey());
			}
		}
	}
}
