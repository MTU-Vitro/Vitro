package vitro.grid;

import vitro.*;
import vitro.util.*;
import java.util.*;

/**
* Hex is a Model implementation representing space as
* a two-dimensional hexagonal grid. Multiple Actors
* can exist in the same grid cell at once by default,
* but this behavior can be modified by overriding
* the passable() predicates.
*
* @author John Earnest
**/
public class Hex extends Grid {

	/**
	* A reference to the current Model.
	**/
	protected final Grid model;

	/**
	* Create a new Hex with a specified size.
	*
	* @param width the width of the Hex.
	* @param height the height of the Hex.
	**/
	public Hex(int width, int height) {
		super(width, height);
		model = this;
	}

	/**
	* Directions corresponding to the six neighbors of a hex.
	**/
	public enum Dir { N, NE, SE, S, SW, NW };

	/**
	* Obtain a reference to a Location object representing a neighboring hex.
	*
	* @param src the origin Location.
	* @param dir the direction of the neighbor.
	* @return a neighboring Location.
	**/
	public Location neighbor(Location src, Dir dir) {
		switch(dir) {
			case N  : return new Location(this, src.x,   src.y-1);
			case NE : return new Location(this, src.x+1, src.x % 2 == 0 ? src.y-1 : src.y  );
			case SE : return new Location(this, src.x+1, src.x % 2 == 0 ? src.y   : src.y+1);
			case S  : return new Location(this, src.x,   src.y+1);
			case SW : return new Location(this, src.x-1, src.x % 2 == 0 ? src.y   : src.y+1);
			case NW : return new Location(this, src.x-1, src.x % 2 == 0 ? src.y-1 : src.y  );

			default: throw new Error("Unknown direction!");
		}
	}

	/**
	* Obtain references to Location objects representing neighboring hexes.
	*
	* @param location the origin Location.
	* @return a Set of neighboring Locations.
	**/
	public Set<Location> neighbors(Location location) {
		Set<Location> ret = new HashSet<Location>();
		for(Dir dir : Dir.values()) {
			Location a = neighbor(location, dir);
			if (a.valid()) { ret.add(a); }
		}
		return ret;
	}

	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to a specified Actor.
	*
	* @param actor the actor to consider moving.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(Actor actor) {
		return passable(actor, neighbors(locations.get(actor)));
	}

	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to any Actor.
	*
	* @param location the origin Location.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(Location location) {
		return passable(null, neighbors(location));
	}
}
