package demos.search;

import java.util.*;

public interface Search<E> {
	public List<E> search(Domain<E> domain);
}
