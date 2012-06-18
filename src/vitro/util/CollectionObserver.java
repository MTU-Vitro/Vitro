package vitro.util;
import java.io.Serializable;

/**
* CollectionObservers can be attached
* to observable collections and receive notification
* when these collections are modified.
*
* @author John Earnest
**/
public interface CollectionObserver<E> extends Serializable {

	/**
	* Fired immediately after an element has been
	* added to an observable collection.
	*
	* @param sender the ObservableCollection that has been modified.
	* @param e the Object that has been added to the collection.
	**/
	public void added(ObservableCollection sender, E e);

	/**
	* Fired immediately after an element has been
	* removed from an observable collection.
	*
	* @param sender the ObservableCollection that has been modified.
	* @param e the Object that has been removed from the collection.
	**/
	public void removed(ObservableCollection sender, E e);
}