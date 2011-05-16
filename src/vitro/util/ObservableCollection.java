package vitro.util;

public interface ObservableCollection<E> {
	public void addObserver(CollectionObserver<E> o);
}