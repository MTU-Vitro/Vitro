package vitro.util;
import java.util.*;

/**
* An ObservableCollection implementing the List
* interface by way of extending AbstractList and
* wrapping an internal List.
*
* @author John Earnest
**/
public class ObservableList<E> extends AbstractList<E> implements ObservableCollection<E> {
	
	private final ObservableCollection host;
	private final List<E> store;
	private final List<CollectionObserver<E>> observers = new ArrayList<CollectionObserver<E>>();
	
	/**
	* Create a new, empty List.
	**/
	public ObservableList() {
		store = new ArrayList<E>();
		host = this;
	}
	
	/**
	* Create a new List with the same elements as another Collection.
	*
	* @param c the source Collection.
	**/
	public ObservableList(Collection<? extends E> c) {
		store = new ArrayList<E>(c);
		host = this;
	}

	/**
	* Create a new List with the same elements as another Collection.
	* Update notifications will be sent to observers as if originating
	* at the supplied host ObservableCollection.
	*
	* @param c the source Collection.
	* @param host the host ObservableCollection
	**/
	public ObservableList(Collection<? extends E> c, ObservableCollection host) {
		store = new ArrayList<E>(c);
		this.host = host;
	}
	
	public void addObserver(CollectionObserver<E> o) {
		observers.add(o);
	}
	
	/**
	* Obtain a reference to the backing store used by this List.
	* Changes to the backing store will not trigger calls
	* to any observers of this collection.
	*
	* @return the internal List.
	**/
	public List<E> store() {
		return store;
	}
	
	public E get(int i) {
		return store.get(i);
	}
	
	public int size() {
		return store.size();
	}
	
	public E remove(int i) {
		E ret = store.remove(i);
		for(CollectionObserver<E> o : observers) {
			o.removed(host, ret);
		}
		return ret;
	}
	
	public void add(int i, E e) {
		store.add(i, e);
		for(CollectionObserver<E> o : observers) {
			o.added(host, e);
		}
	}
	
	public E set(int i, E e) {
		E old = store.get(i);
		E ret = store.set(i, e);
		if (old != e) {
			for(CollectionObserver<E> o : observers) {
				o.removed(host, old);
				o.added(host, e);
			}
		}
		return ret;
	}
}