package vitro.grid;

import vitro.*;
import java.util.*;

/**
* GridLabelAnnotations allow users to associate a textual label
* with Locations within a Grid.
*
* @author John Earnest
**/
public class GridLabelAnnotation implements Annotation {

	/**
	* This GridLabelAnnotation's Location-Label associations.
	**/
	public final Map<Location, String> labels = new HashMap<Location, String>();

	/**
	* Construct a new, empty GridLabelAnnotation.
	**/
	public GridLabelAnnotation() {

	}

	/**
	* Construct a GridLabelAnnotation pre-initialized with a set of labels.
	*
	* @param labels a set of Location-Label associations.
	**/
	public GridLabelAnnotation(Map<Location, String> labels) {
		this.labels.putAll(labels);
	}
}