package vitro.grid;

import vitro.*;
import java.awt.Color;
import java.util.*;

public class VectorAnnotation implements Annotation {
	public final Map<Location, Integer> dirs = new HashMap<Location, Integer>();
	public final Color color;

	public VectorAnnotation(Color color) {
		this.color = color;
	}

	public VectorAnnotation(Color color, Map<Location, Integer> dirs) {
		this(color);
		this.dirs.putAll(dirs);
	}
}