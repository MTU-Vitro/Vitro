package vitro.grid;

import vitro.*;
import java.util.*;
import java.awt.*;

public class GridAnnotation implements Annotation {
	public final Map<Point, Color> coloring = new HashMap<Point, Color>();
	
	
	public GridAnnotation(Collection<Location> colored, Color color) {
		for(Location location : colored) { coloring.put(new Point(location.x, location.y), color); }
	}
	
	
	/*
	public GridAnnotation(Collection<Point> colored, Color color) {
		for(Point point : colored) { coloring.put(point, color); }
	}
	*/
	
	
	public GridAnnotation(Map<Location, Color> colored) {
		for(Location location : colored.keySet()) { coloring.put(new Point(location.x, location.y), colored.get(location)); }
	}
	
	
	/*
	public GridAnnotation(Map<Point, Color> colored) {
		for(Point point : colored.keySet()) { coloring.put(point, colored.get(point)); }
	}
	*/
	
	
	public GridAnnotation(Map<Location, ? extends Number> scaling, Color colorMin, Color colorMax) {
		this(calculateScaling(scaling, colorMin, colorMax));
	}
	
	
	/*
	public GridAnnotation(Map<Point, ? extends Number> scaling, Color colorMin, Color colorMax) {
		this(calculateScaling(scaling, colorMin, colorMax));
	}
	*/
	
	protected static <K> Map<K, Color> calculateScaling(Map<K, ? extends Number> scaling, Color colorMin, Color colorMax) {
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for(Number num : scaling.values()) {
			min = Math.min(min, num.floatValue());
			max = Math.max(max, num.floatValue());
		}

		float[] compMin = colorMin.getColorComponents(null);
		float[] compMax = colorMax.getColorComponents(null);
		
		Map<K, Color> colors = new HashMap<K, Color>();
		for(K key : scaling.keySet()) {
			if(min != max) {
				float s = (scaling.get(key).floatValue() - min) / (max - min);
				float[] comp = new float[] {
					s * (compMax[0] - compMin[0]) + compMin[0],
					s * (compMax[1] - compMin[1]) + compMin[1],
					s * (compMax[2] - compMin[2]) + compMin[2],
				};
				float alpha = ((s * (colorMax.getAlpha() - colorMin.getAlpha())) + colorMin.getAlpha()) / 255;
				colors.put(key, new Color(comp[0], comp[1], comp[2], alpha));
			}
			else {
				colors.put(key, colorMin);
			}
		}
		
		return colors;
	}
}
