package vitro.model;

public interface Action {
	public void apply();
	public void undo();
}