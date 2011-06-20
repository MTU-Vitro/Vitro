package vitro;

import java.util.*;
import static vitro.util.Groups.*;

public class RandomAgent<A extends Actor> implements Agent<A> {

	public Action choose(A actor, Set<Action> options) {
		return any(options);
	}
}