package vitro.grid;

import vitro.*;
import java.awt.Color;
import java.util.*;

public class VectorAnnotation implements Annotation {
	public final Map<Location, Double> dirs = new HashMap<Location, Double>();
	public final Color color;

	public VectorAnnotation(Color color) {
		this.color = color;
	}

	public VectorAnnotation(Color color, Map<Location, Double> dirs) {
		this(color);
		this.dirs.putAll(dirs);
	}
}