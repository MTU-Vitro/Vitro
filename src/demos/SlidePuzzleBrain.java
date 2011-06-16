package demos;

import vitro.*;
import vitro.grid.*;
import java.util.*;

public class SlidePuzzleBrain implements Agent<SlidePuzzle.Gap> {
	
	public Action choose(SlidePuzzle.Gap actor, Set<Action> options) {

		// for now, let's just flail randomly.
		return vitro.util.Groups.any(options);
	}
}