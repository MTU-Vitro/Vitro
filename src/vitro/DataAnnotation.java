package vitro;

/**
* An Annotation meant for associating an arbitrary
* data structure with the state of the Model.
* DataAnnotation can render an arbitrary tree constructed
* from Maps, Collections, arrays, primitives and
* synthetic Objects in a graphical format.
* For best results, use a Map from "category names"
* to values as a base-level element.
*
* Views may implement specific logic for this
* type of Annotation, but the default Host implementation
* can pick up DataAnnotations and break them off into
* their own window, allowing DataAnnotations
* to be used with almost any View "out of the box".
*
* @author John Earnest
**/
public class DataAnnotation implements Annotation {

	public final Object data;
	public final String label;

	/**
	* Construct a new DataAnnotation from
	* a data structure.
	*
	* @param data the root of any tree of data structures
	**/
	public DataAnnotation(Object data) {
		this(data, null);
	}

	/**
	* Construct a new DataAnnotation with
	* an identifying label.
	*
	* @param data the root of any tree of data structures
	* @param label the label to associate with these data structures
	**/
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