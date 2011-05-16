package vitro.util;
import java.util.*;

public class ObservableSet<E> extends AbstractSet<E> implements ObservableCollection<E> {
	
	private final Set<E> store;
	private final List<CollectionObserver<E>> observers = new ArrayList<CollectionObserver<E>>();

	public ObservableSet() {
		store = new HashSet<E>();
	}
	
	public ObservableSet(Collection<? extends E> c) {
		store = new HashSet<E>(c);
	}
	
	public void addObserver(CollectionObserver<E> o) {
		observers.add(o);
	}
	
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