package vitro;

public interface Persistent {

	public Object freeze(Model m);

	public void thaw(Object o);

}