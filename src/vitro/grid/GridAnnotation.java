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
	
	/**
	* Create a new GridAnnotation.
	*
	* @param colored a mapping from Locations to their desired Color.
	**/
	public GridAnnotation(Map<Location, Color> colored) {
		for(Location location : colored.keySet()) { coloring.put(new Point(location.x, location.y), colored.get(location)); }
	}
	
	/**
	* Create a new GridAnnotation.
	* Colors will range on a gradient between a minimum and maximum
	* color as dictated by the range of values associated with Locations.
	*
	* @param scaling the values associated with specific Locations.
	* @param colorMin the color to assign to the lowest value in values from scaling.
	* @param colorMax the color to assign to the highest value in value from scaling.
	**/
	public GridAnnotation(Map<Location, ? extends Number> scaling, Color colorMin, Color... colorInt) {
		this(calculateScaling(scaling, colorMin, colorInt));
	}
	
	/**
	* Generate a scaled gradient between two colors based on the
	* values specified in the "scaling" table.
	*
	* @param scaling the values associated with specific Locations.
	* @param colorMin the color to assign to the lowest value in values from scaling.
	* @param colorMax the color to assign to the highest value in value from scaling.
	* @return a mapping from objects to their scaled colors.
	**/
	protected static <K> Map<K, Color> calculateScaling(Map<K, ? extends Number> scaling, Color colorMin, Color... colorInt) {
		Comparator<Number> comparator = new Comparator<Number>() {
			public int compare(Number first, Number second) {
				return Float.compare(first.floatValue(), second.floatValue());
			}
		};
	
		float min = Collections.min(scaling.values(), comparator).floatValue();
		float max = Collections.max(scaling.values(), comparator).floatValue() + 0.001f;

		//
		float[][] components = new float[colorInt.length + 1][];
		components[0] = colorMin.getColorComponents(null);
		
		float[] alphas = new float[colorInt.length + 1];
		alphas[0] = colorMin.getAlpha() / 255f;
		
		for(int x = 0; x < colorInt.length; x++) {
			components[x + 1] = colorInt[x].getColorComponents(null);
			alphas[x + 1] = colorInt[x].getAlpha() / 255f;
		}
		
		//
		Map<K, Color> colors = new HashMap<K, Color>();
		for(K key : scaling.keySet()) {
			if(min != max) {
				float value = colorInt.length * (scaling.get(key).floatValue() - min) / (max - min);
				int   index = (int)value;
				float scale = value - index;

				float[] comp = new float[] {
					scale * (components[index + 1][0] - components[index][0]) + components[index][0],
					scale * (components[index + 1][1] - components[index][1]) + components[index][1],
					scale * (components[index + 1][2] - components[index][2]) + components[index][2]
				};
				float alpha = scale * (alphas[index + 1] - alphas[index]) + alphas[index];
				
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
