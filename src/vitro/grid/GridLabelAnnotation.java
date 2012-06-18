package vitro.grid;

import vitro.*;
import java.util.*;

public class GridLabelAnnotation implements Annotation {
	public final Map<Location, String> labels = new HashMap<Location, String>();

	public GridLabelAnnotation() {

	}

	public GridLabelAnnotation(Map<Location, String> labels) {
		this.labels.putAll(labels);
	}
}