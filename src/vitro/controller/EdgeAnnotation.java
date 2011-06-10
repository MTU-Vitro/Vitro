package vitro.graph;

import vitro.*;

public class EdgeAnnotation implements Annotation {

	public final Edge edge;
	public final String label;

	public EdgeAnnotation(Edge edge) {
		this(edge, null);
	}
	
	public EdgeAnnotation(Edge edge, String label) {
		this.edge = edge;
		this.label = label;
	}

	@Override
	public String toString() {
		if (label != null) { return label; }
		return super.toString();
	}
}