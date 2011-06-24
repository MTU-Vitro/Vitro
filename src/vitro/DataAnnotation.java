package vitro;

public class DataAnnotation implements Annotation {

	public final Object data;
	public final String label;

	public DataAnnotation(Object data) {
		this(data, null);
	}

	public DataAnnotation(Object data, String label) {
		this.data  = data;
		this.label = label;
	}

	@Override
	public String toString() {
		if (label != null) { return label; }
		return data.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DataAnnotation)) { return false; }
		DataAnnotation other = (DataAnnotation)o;
		if (!data.equals(other.data)) { return false; }
		if (label == null) { return other.label == null; }
		else { return label.equals(other.label); }
	}

	@Override
	public int hashCode() {
		if (label == null) { return data.hashCode(); }
		return data.hashCode() ^ label.hashCode();
	}

}