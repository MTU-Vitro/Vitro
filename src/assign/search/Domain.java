package assign.search;

import java.util.*;

public interface Domain<E> {
	
	public E initial();
	public E goal();
	
	public Set<E> expand(E e);
}
