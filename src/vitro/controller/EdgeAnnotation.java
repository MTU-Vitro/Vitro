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

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EdgeAnnotation)) { return false; }
		EdgeAnnotation other = (EdgeAnnotation)o;
		if (!edge.equals(other.edge)) { return false; }
		if (label == null) { return other.label == null; }
		else { return label.equals(other.label); }
	}

	@Override
	public int hashCode() {
		if (label == null) { return edge.hashCode(); }
		return edge.hashCode() ^ label.hashCode();
	}

}