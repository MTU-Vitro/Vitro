package vitro.util;
import java.util.*;

public class Groups {

	private static final Random rand = new Random();


	public static <S, C extends S> S firstOfType(Class<C> c, Collection<S> source) {
		for(S a : source) {
			if (c.equals(a.getClass())) { return a; }
		}
		return null;
	}

	public static <S, C extends S> List<S> ofType(Class<C> c, Collection<S> source) {
		List<S> ret = new ArrayList<S>();
		for(S a : source) {
			if (c.equals(a.getClass())) { ret.add(a); }
		}
		return ret;
	}

	public static <S, C extends S> boolean containsType(Class<C> c, Collection<S> source) {
		for(S a : source) {
			if (c.equals(a.getClass())) { return true; }
		}
		return false;
	}

	public static <S> S any(Collection<S> source) {
		// this is not the most efficient approach,
		// but it's simple. Improve it later if necessary.
		List<S> group = new ArrayList<S>(source);
		return group.get(rand.nextInt(group.size()));
	}
}