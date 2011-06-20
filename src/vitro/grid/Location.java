package vitro.grid;

import vitro.*;
import java.util.*;

public class Location {
	private final Grid g;
	public final int x;
	public final int y;
	
	public Location(Grid g, int x, int y) {
		this.g = g;
		this.x = x;
		this.y = y;
	}

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

	public Location add(int x, int y) {
		return new Location(g, this.x + x, this.y + y);
	}

	public boolean passable(Actor a) {
		return g.passable(a, this);
	}
}