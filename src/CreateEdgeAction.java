public class CreateEdgeAction extends GraphAction {

	private final Graph.Node start;
	private final Graph.Node end;
	private Graph.Edge e = null;

	public CreateEdgeAction(Graph model, Graph.Node start, Graph.Node end) {
		super(model);
		this.start = start;
		this.end   = end;
	}

	public void apply() {
		if (e == null) { e = model.createEdge(start, end); }
		else { model.edges.add(e); }
	}

	public void undo() {
		model.edges.remove(e);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		       start.hashCode() ^
		         end.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateEdgeAction)) { return false; }
		CreateEdgeAction other = (CreateEdgeAction)o;
		return (other.model == this.model) &&
		       (other.start == this.start) &&
		       (other.end   == this.end  );
	}

}