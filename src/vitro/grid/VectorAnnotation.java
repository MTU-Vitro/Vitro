package vitro.grid;

import vitro.*;
import java.awt.Color;
import java.util.*;

/**
* VectorAnnotations allow users to overlay a flow field
* on selected cells of a Grid. Vector directions are specified
* as a value between 0 and 1, clockwise from "north".
*
* @author John Earnest
**/

public class VectorAnnotation implements Annotation {

	/**
	* This VectorAnnotation's Location-vector associations.
	**/
	public final Map<Location, Double> dirs = new HashMap<Location, Double>();

	/**
	* Construct a new, empty VectorAnnotation.
	**/
	public VectorAnnotation() {
		
	}

	/**
	* Construct a VectorAnnotation pre-initialized with a set of vectors.
	*
	* @param dirs a set of Location-vector associations.
	**/
	public VectorAnnotation(Map<Location, Double> dirs) {
		this.dirs.putAll(dirs);
	}
}