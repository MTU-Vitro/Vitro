package assign.search;

import vitro.grid.*;

public class SokobanStateBLU {
	public final Location bluLocation;
	public final Location blockLocation;
	
	public SokobanStateBLU(Location bluLocation, Location blockLocation) {
		this.bluLocation   = bluLocation;
		this.blockLocation = blockLocation;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SokobanStateBLU)) { return false; }
		SokobanStateBLU other = (SokobanStateBLU)o;
		// Ignoring BLU location. Doesn't matter to the problem.
		return other.blockLocation.equals(blockLocation);
	}
	
	@Override
	public int hashCode() {
		return bluLocation.hashCode();
	}
	
	@Override
	public String toString() {
		return bluLocation + "  " + blockLocation;
	}
}
