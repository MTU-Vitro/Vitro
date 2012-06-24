package vitro.util;
import java.util.*;

/**
* ReversibleMap provides a convenient way
* to maintain a two-way map between keys and values.
*
* @author John Earnest
**/

public class ReversibleMap<K, V> extends ObservableMap<K, V> {

	private final ReversibleMap<V, K> reverse;

	/**
	* Create a new, empty Map.
	**/
	public ReversibleMap() {
		reverse = new ReversibleMap<V, K>(this);
		reverse.addObserver(new MirrorObserver<V, K>(this.store()));
	}
	
	/**
	* Create a new Map with the same elements as another Map.
	*
	* @param c the source Map.
	**/
	public ReversibleMap(Map<? extends K, ? extends V> c) {
		reverse = new ReversibleMap<V, K>(this);
		for(Map.Entry<? extends K, ? extends V> entry : c.entrySet()) {
			reverse.put(entry.getValue(), entry.getKey());
		}
		reverse.addObserver(new MirrorObserver<V, K>(this.store()));
	}

	private ReversibleMap(ReversibleMap<V, K> reverse) {
		this.reverse = reverse;
		reverse.addObserver(new MirrorObserver<V, K>(this.store()));
	}

	/**
	* Obtain a reference to this Map's inverse.
	**/
	public ReversibleMap<V, K> reverse() {
		return reverse;
	}

	private static class MirrorObserver<B, A> implements CollectionObserver<Map.Entry<B, A>> {
		private final Map<A, B> store;

		public MirrorObserver(Map<A, B> store) {
			this.store = store;
		}

		public void added(ObservableCollection sender, Map.Entry<B, A> entry) {
			store.put(entry.getValue(), entry.getKey());
		}

		public void removed(ObservableCollection sender, Map.Entry<B, A> entry) {
			store.remove(entry.getValue());
		}
	}
}