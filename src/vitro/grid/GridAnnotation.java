package vitro.grid;

import vitro.*;
import java.util.*;
import java.awt.*;

/**
* GridAnnotations allow users to overlay an array of Colors
* on their Grid's View.
*
* @author Jason Hiebel
**/
public class GridAnnotation implements Annotation, Comparable {
	private static Integer currentOrder = 0;

	/**
	* A mapping between positions on the Grid and their Color.
	**/
	public final Map<Point, Color> coloring = new HashMap<Point, Color>();
	private Integer order;
	
	/**
	* Create a new GridAnnotation.
	*
	* @param colored the Locations to Color.
	* @param color a Color to apply to every specified Location.
	**/
	public GridAnnotation(Collection<Location> colored, Color color) {
		order = currentOrder++;
		for(Location location : colored) { coloring.put(new Point(location.x, location.y), color); }
	}
	
	
	/*
	public GridAnnotation(Collection<Point> colored, Color color) {
		for(Point point : colored) { coloring.put(point, color); }
	}
	*/
	
	/**
	* Create a new GridAnnotation.
	*
	* @param colored a mapping from Locations to their desired Color.
	**/
	public GridAnnotation(Map<Location, Color> colored) {
		for(Location location : colored.keySet()) { coloring.put(new Point(location.x, location.y), colored.get(location)); }
	}
	
	
	/*
	public GridAnnotation(Map<Point, Color> colored) {
		for(Point point : colored.keySet()) { coloring.put(point, colored.get(point)); }
	}
	*/
	
	/**
	* Create a new GridAnnotation.
	* Colors will range on a gradient between a minimum and maximum
	* color as dictated by the range of values associated with Locations.
	*
	* @param scaling the values associated with specific Locations.
	* @param colorMin the color to assign to the lowest value in values from scaling.
	* @param colorMax the color to assign to the highest value in value from scaling.
	**/
	public GridAnnotation(Map<Location, ? extends Number> scaling, Color colorMin, Color colorMax) {
		this(calculateScaling(scaling, colorMin, colorMax));
	}
	
	
	/*
	public GridAnnotation(Map<Point, ? extends Number> scaling, Color colorMin, Color colorMax) {
		this(calculateScaling(scaling, colorMin, colorMax));
	}
	*/
	
	/**
	* Generate a scaled gradient between two colors based on the
	* values specified in the "scaling" table.
	*
	* @param scaling the values associated with specific Locations.
	* @param colorMin the color to assign to the lowest value in values from scaling.
	* @param colorMax the color to assign to the highest value in value from scaling.
	* @return a mapping from objects to their scaled colors.
	**/
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
	
	public int compareTo(Object o) {
		if(o instanceof GridAnnotation) { return ((GridAnnotation)o).order.compareTo(order); }
		return 1;
	}
}
