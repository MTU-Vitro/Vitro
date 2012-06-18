package vitro.graph;

import vitro.*;

public abstract class Edge {
	public final Node start;
	public final Node end;
	
	public Edge(Node start, Node end) {
		this.start = start;
		this.end   = end;
	}

	public String toString() {
		return String.format("('%s'-->'%s')", start, end);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Edge)) { return false; }
		Edge other = (Edge)o;
		return start.equals(other.start) &&
		         end.equals(other.end);
	}

	public int hashCode() {
		return start.hashCode() ^
		         end.hashCode();
	}
}