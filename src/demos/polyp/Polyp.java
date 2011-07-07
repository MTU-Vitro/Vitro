package demos.polyp;

import vitro.*;
import vitro.plane.*;

public class Polyp extends Plane {

	public Polyp() {
		for(int x = 0; x < 10; x++) {
			Cell cell = new Cell(this);
			positions.put(cell, new Position(Math.random() * 600 + 20, Math.random() * 440 + 20));
		}
	}
	
	public boolean done() { return false; }
}
