package vitro.model.graph;

public abstract class Edge {
	public final Node start;
	public final Node end;
	
	public Edge(Node a, Node b) {
		start = a;
		end   = b;
	}

	public String toString() {
		return String.format("('%s'-->'%s')", start, end);
	}
}