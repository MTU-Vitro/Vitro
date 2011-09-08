package vitro.grid;

import vitro.*;
import java.util.*;

/**
* A Location represents a position on a Grid.
*
* @author John Earnest
**/
public class Location {
	private final Grid g;

	/**
	* The grid column of this Location.
	**/
	public final int x;
	/**
	* The grid row of this Location.
	**/
	public final int y;
	
	/**
	* Construct a new Location.
	*
	* @param g the target Grid.
	* @param x the grid column of this Location.
	* @param y the grid row of this Location.
	**/
	public Location(Grid g, int x, int y) {
		this.g = g;
		this.x = x;
		this.y = y;
	}

	/**
	* Confirm this Location is within the bounds of its Grid.
	*
	* @return true if this Location is in bounds.
	**/
	public boolean valid() {
		return x >= 0 && x < g.width && y >= 0 && y < g.height;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Location)) { return false; }
		Location other = (Location)o;
		return other.g == g && other.x == x && other.y == y;
	}

	@Override
	public int hashCode() {
		return g.hashCode() ^
				new Integer(x).hashCode() ^
				new Integer(y).hashCode();
	}

	public String toString() {
		return String.format("(%d, %d)", x, y);
	}

	/**
	* Create a new Location relative to this one given X and Y offsets.
	*
	* @param x the x-offset of the new Location.
	* @param y the y-offset of the new Location.
	* @return the relative Location.
	**/
	public Location add(int x, int y) {
		return new Location(g, this.x + x, this.y + y);
	}

	/**
	* Check the passability of this Location respecting the
	* passable() predicate of the parent Grid.
	*
	* @param a the Actor to check.
	* @return true if the specified Actor can move to this Location.
	**/
	public boolean passable(Actor a) {
		return g.passable(a, this);
	}
}