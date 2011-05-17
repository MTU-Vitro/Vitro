package vitro.util;
import java.util.*;

/**
* An ObservableCollection implementing the Set
* interface by way of extending AbstractSet and
* wrapping an internal Set.
*
* @author John Earnest
**/
public class ObservableSet<E> extends AbstractSet<E> implements ObservableCollection<E> {
	
	private final Set<E> store;
	private final List<CollectionObserver<E>> observers = new ArrayList<CollectionObserver<E>>();

	/**
	* Create a new, empty Set.
	**/
	public ObservableSet() {
		store = new HashSet<E>();
	}
	
	/**
	* Create a new Set with the same elements as another Collection.
	*
	* @param c the source Collection.
	**/
	public ObservableSet(Collection<? extends E> c) {
		store = new HashSet<E>(c);
	}
	
	public void addObserver(CollectionObserver<E> o) {
		observers.add(o);
	}
	
	/**
	* Obtain a reference to the backing store used by this Set.
	* Changes to the backing store will not trigger calls
	* to any observers of this collection.
	*
	* @return the internal Set.
	**/
	public Set<E> store() {
		return store;
	}

	public int size() {
		return store.size();
	}

	public Iterator<E> iterator() {
		return new ObservableSetIterator(this);
	}

	private class ObservableSetIterator implements Iterator<E> {
		private final ObservableSet<E> set;
		private final Iterator<E> iterator;
		private E previous;
		
		private ObservableSetIterator(ObservableSet<E> set) {
			this.set = set;
			this.iterator = store.iterator();
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public E next() {
			previous = iterator.next();
			return previous;
		}

		public void remove() {
			iterator.remove();
			for(CollectionObserver<E> observer : observers) {
				observer.removed(set, previous);
			}
		}
	}

	public boolean add(E o) {
		boolean ret = store.add(o);
		for(CollectionObserver<E> observer : observers) {
			observer.added(this, o);
		}
		return ret;
	}
}