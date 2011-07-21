package vitro.grid;

import vitro.*;
import java.util.*;
import java.awt.*;

public class GridAnnotation implements Annotation {
	public final Map<Location, Color> coloring;
	
	public GridAnnotation(Map<Location, Color> coloring) {
		this.coloring = coloring;
	}
	
	public GridAnnotation(Map<Location, ? extends Number> scaling, Color colorMin, Color colorMax) {
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for(Number num : scaling.values()) {
			min = Math.min(min, num.floatValue());
			max = Math.max(max, num.floatValue());
		}
		
		float[] compMin = colorMin.getColorComponents(null);
		float[] compMax = colorMax.getColorComponents(null);
		
		coloring = new HashMap<Location, Color>();
		for(Location location : scaling.keySet()) {
		
			if(min != max) {
				float s = (scaling.get(location).floatValue() - min) / (max - min);
				float[] comp = new float[] {
					s * (compMax[0] - compMin[0]) + compMin[0],
					s * (compMax[1] - compMin[1]) + compMin[1],
					s * (compMax[2] - compMin[2]) + compMin[2]
				};
				
				coloring.put(location, new Color(comp[0], comp[1], comp[2]));
			}
			else {
				coloring.put(location, colorMin);
			}
		}
	}
}
