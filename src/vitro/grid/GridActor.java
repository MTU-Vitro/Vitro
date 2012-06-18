package vitro.grid;

import vitro.*;
import java.util.*;
import static vitro.util.Groups.*;

/**
* GridActor provides a number of convenience
* methods for Actors that are meant
* to be part of a Grid model.
*
* @author John Earnest
**/
public class GridActor extends Actor {
	
	/**
	* A set of x/y deltas for vertically or horizontally adjacent cells.
	**/
	protected static final int[][] ORTHOGONAL = Grid.ORTHOGONAL;
	/**
	* A set of x/y deltas for diagonally adjacent cells.
	**/
	protected static final int[][] DIAGONAL   = Grid.DIAGONAL;
	/**
	* A set of x/y deltas for vertically, horizontally and diagonally adjacent cells.
	**/
	protected static final int[][] ADJACENT   = Grid.ADJACENT;

	/**
	* A reference to this Actor's Model.
	**/
	protected final Grid model;

	/**
	* Build a new GridActor associated with a specific Model.
	**/
	public GridActor(Grid model) {
		this.model = model;
	}

	/**
	* Get the Location of this Actor in the current Model.
	*
	* @return this Actor's Location.
	**/
	public Location location() {
		return model.locations.get(this);
	}

	/**
	* Obtain references to Location objects representing neighboring cells.
	* See Grid.neighbors().
	*
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of neighboring Locations.
	**/
	public Set<Location> neighbors(int[][] deltas) {
		return model.neighbors(location(), deltas);
	}

	/**
	* Obtain references to Location objects representing cells neighboring a specific Location.
	* See Grid.neighbors().
	*
	* @param location the origin Location.
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of neighboring Locations.
	**/
	public Set<Location> neighbors(Location location, int[][] deltas) {
		return model.neighbors(location, deltas);
	}

	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to a this Actor.
	*
	* @param location the origin Location.
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(Location location, int[][] deltas) {
		return model.passableNeighbors(location, deltas);
	}

	/**
	* Performs the same function as neighbors(), but only returns Locations which
	* would be passable to a this Actor.
	* Deltas are treated as relative to this Actor.
	*
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> passableNeighbors(int[][] deltas) {
		return model.passable(this, neighbors(deltas));
	}

	/**
	* Performs the same function as neighbors(), but will "pump on"
	* a delta for as long as the resulting Location is passable to this
	* Actor. For example, assuming a passable() method which disallows
	* multiple Actors in the same Grid cell, given DIAGONAL as a set
	* of deltas this method would return valid non-capturing moves
	* for a chess Bishop.
	*
	* @param deltas a collection of x and y offsets to neighboring cells.
	* @return a Set of passable neighboring Locations.
	**/
	public Set<Location> pumpingNeighbors(int[][] deltas) {
		Set<Location> ret = new HashSet<Location>();
		for(int[] d : deltas) {
			int x = location().x;
			int y = location().y;
			while(true) {
				x += d[0];
				y += d[1];
				if (x < 0 || x >= model.width || y < 0 || y >= model.height) { break; }
				Location location = new Location(model, x, y);
				if (!model.passable(this, location)) { break; }
				ret.add(new Location(model, x, y));
			}
		}
		return ret;
	}

	/**
	* Produce a Set of MoveActions corresponding to moving
	* this Actor to each of a Set of Locations.
	*
	* @param locations the Locations to which this Actor might move.
	* @return a Set of Moves for this Actor.
	**/
	public Set<Action> moves(Set<Location> locations) {
		Set<Action> ret = new HashSet<Action>();
		for(Location location : locations) {
			ret.add(new MoveAction(model, location, this));
		}
		return ret;
	}

	/**
	* Find an Action corresponding to moving this Actor to
	* a specific Location.
	*
	* @param location the Actor's desired destination.
	* @param options a Set of Actions to consider.
	* @return the found MoveAction or null if none is available.
	**/
	public MoveAction move(Location location, Set<Action> options) {
		for(Action action : ofType(MoveAction.class, options)) {
			MoveAction move = (MoveAction)action;
			if (move.actor == this && move.end.equals(location)) { return move; }
		}
		return null;
	}

	/*
	void moveToward() {}
	void create() {}
	void destroy() {}
	*/
}
