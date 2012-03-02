package assign.search;

import java.util.*;

public interface Domain<E> {
	public E       initial();
	public boolean isGoal(E e);
	public double  cost(E head, E tail);
	public Set<E>  expand(E e);
}
