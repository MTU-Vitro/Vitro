package vitro.util;
import java.util.*;

/**
* An ObservableCollection implementing the Map
* interface by way of extending AbstractMap and
* wrapping an internal Map.
*
* @author John Earnest
**/

public class ObservableMap<K, V> extends AbstractMap<K, V> implements ObservableCollection<Map.Entry<K, V>> {
	
	private final ObservableCollection host;
	private final Map<K, V> store;
	private final List<CollectionObserver<Map.Entry<K, V>>> observers = new ArrayList<CollectionObserver<Map.Entry<K, V>>>();

	/**
	* Create a new, empty Map.
	**/
	public ObservableMap() {
		store = new HashMap<K, V>();
		host = this;
	}
	
	/**
	* Create a new Map with the same elements as another Map.
	*
	* @param c the source Map.
	**/
	public ObservableMap(Map<? extends K, ? extends V> c) {
		store = new HashMap<K, V>(c);
		host = this;
	}

	/**
	* Create a new Map with the same elements as another Map.
	* Update notifications will be sent to observers as if originating
	* at the supplied host ObservableCollection.
	*
	* @param c the source Collection.
	* @param host the host ObservableCollection
	**/
	public ObservableMap(Map<? extends K, ? extends V> c, ObservableCollection host) {
		store = new HashMap<K, V>(c);
		this.host = host;
	}
	
	public void addObserver(CollectionObserver<Map.Entry<K, V>> o) {
		observers.add(o);
	}

	/**
	* Obtain a reference to the backing store used by this Map.
	* Changes to the backing store will not trigger calls
	* to any observers of this collection.
	*
	* @return the internal Map.
	**/
	public Map<K, V> store() {
		return store;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		ObservableSet<Map.Entry<K, V>> entries = new ObservableSet<Map.Entry<K, V>>(store.entrySet(), host);
		for(CollectionObserver<Map.Entry<K, V>> o : observers) {
			entries.addObserver(o);
		}
		return entries;
	}

	public V put(K key, V value) {
		V old = store.put(key, value);
		if (old != value) {
			ObservableMapEntry<K, V> removed = new ObservableMapEntry<K, V>(key, old);
			ObservableMapEntry<K, V> created = new ObservableMapEntry<K, V>(key, value);
			for(CollectionObserver<Map.Entry<K, V>> o : observers) {
				o.removed(host, removed);
				o.added(host, created);
			}
		}
		return old;
	}

	private static class ObservableMapEntry<A, B> implements Map.Entry<A, B> {
		private final A key;
		private final B value;

		public ObservableMapEntry(A key, B value) {
			this.key   = key;
			this.value = value;
		}

		public A getKey()   {
			return key;
		}

		public B getValue() {
			return value;
		}

		public B setValue(B value) {
			throw new UnsupportedOperationException();
		}

		// see: http://download.oracle.com/javase/1,5.0/docs/api/java/util/Map.Entry.html#hashCode%28%29
		public int hashCode() {
			return (  key==null ? 0 :   key.hashCode()) ^
			       (value==null ? 0 : value.hashCode());
		}

		// see: http://download.oracle.com/javase/1,5.0/docs/api/java/util/Map.Entry.html#equals%28java.lang.Object%29
		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) { return false; }
			Map.Entry other = (Map.Entry)o;
			return (  key == null ? other.getKey()   == null :   key.equals(other.getKey()))  &&
			       (value == null ? other.getValue() == null : value.equals(other.getValue()));
		}
	}
}