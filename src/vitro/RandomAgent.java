package vitro;

import java.util.*;
import static vitro.util.Groups.*;

/**
* A generic Agent implementation which always
* chooses randomly from the available options.
* Frequently useful for debugging.
*
* @author John Earnest
**/
public class RandomAgent<A extends Actor> implements Agent<A> {

	public Action choose(A actor, Set<Action> options) {
		return any(options);
	}
}