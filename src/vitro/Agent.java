package vitro;

import java.util.*;

public interface Agent<A extends Actor> {

	public Action choose(A actor, Set<Action> options);

}