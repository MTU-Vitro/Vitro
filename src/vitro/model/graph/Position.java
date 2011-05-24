package vitro.model.graph;

public class Position {

	private final Node node;

	public Position(Node node) {
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Position)) { return false; }
		Position other = (Position)o;
		return node.equals(other.node);
	}

	@Override
	public int hashCode() {
		return node.hashCode();
	}

}