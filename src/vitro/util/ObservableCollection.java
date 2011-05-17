package vitro.util;

/**
* ObservableCollections can be used whenever
* it is useful to monitor mutation of Collections.
*
* @author John Earnest
**/
public interface ObservableCollection<E> {
	
	/**
	* Register a CollectionObserver with this Collection.
	* The callbacks provided in the CollectionObserver
	* interface will be fired whenever elements are added
	* to or removed from this Collection.
	*
	* @param o the CollectionObserver to register.
	**/
	public void addObserver(CollectionObserver<E> o);
}