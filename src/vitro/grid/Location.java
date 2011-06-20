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
		if (x < 0 || x >= g.width || y < 0 || y >= g.height) {
			throw new IllegalArgumentException(
				String.format("Location out of bounds: (%d, %d)", x, y)
			);
		}
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
		int nx = Math.min(g.width  - 1, Math.max(this.x + x, 0));
		int ny = Math.min(g.height - 1, Math.max(this.y + y, 0));
		return new Location(g, nx, ny);
	}

	public boolean passable(Actor a) {
		return g.passable(a, this);
	}
}