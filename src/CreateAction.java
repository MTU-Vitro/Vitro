public class CreateAction extends GraphAction {

	private final Graph.Node n;
	private final Actor a;

	public CreateAction(Graph model, Graph.Node n, Actor a) {
		super(model);
		this.n = n;
		this.a = a;
	}

	public void apply() {
		n.actors.add(a);
	}

	public void undo() {
		n.actors.remove(a);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		           n.hashCode() ^
		           a.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateAction)) { return false; }
		CreateAction other = (CreateAction)o;
		return (other.model == this.model) &&
		       (other.n     == this.n    ) &&
		       (other.a     == this.a    );
	}
}