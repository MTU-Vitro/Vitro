package vitro.util;

public interface CollectionObserver<E> {
	public void added(ObservableCollection sender, E e);
	public void removed(ObservableCollection sender, E e);
}