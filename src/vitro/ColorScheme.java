package vitro;

import java.awt.Graphics;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
* ColorSchemes collect a group of Colors
* together and provide an easy way to make
* Views customizable.
*
* @author John Earnest
**/

public class ColorScheme {

	public Color outline;
	public Color secondary;
	public Color background;
	public Color inactive;

	// heading
	// label

	/**
	* Create a new ColorScheme with a default grayscale palette.
	**/
	public ColorScheme() {
		this(
			Color.BLACK,
			Color.GRAY,
			Color.WHITE
		);
	}

	/**
	* Create a new ColorScheme with specified theme colors.
	*
	* @param outline the color to use for drawing shapes and text.
	* @param secondary the color to use for less important or prominent information.
	* @param background the background color for the associated View.
	**/
	public ColorScheme(Color outline, Color secondary, Color background) {
		this.outline    = outline;
		this.secondary  = secondary;
		this.background = background;
		this.inactive   = Color.LIGHT_GRAY;
	}

	public final Map<Object, Color> uniqueColors = new HashMap<Object, Color>();

	/**
	* Manually configure the key color for a specific object.
	*
	* @param o the object to associate with a color.
	* @param color the color to assign to the object.
	**/
	public void setColor(Object o, Color color) {
		uniqueColors.put(o, color);
	}

	/**
	* Produce a unique, consistent color based on an object reference.
	* Colors will attempt to be as visually distinct as possible.
	* If an object already as an associated color stored, that color
	* will be returned. Otherwise a new mapping will be created.
	*
	* @param o the object to associate with a color.
	* @return a unique color.
	**/
	public Color unique(Object o) {
		if (!uniqueColors.containsKey(o)) {
			Random rand = new Random();
			Color  bestColor = null;
			double bestDelta = -1;

			for(int x = 0; x < 10; x++) {
				Color unique = colorFromHash(rand.nextInt());
				int   delta  = 0;
				for(Color other : uniqueColors.values()) {
					delta += colorDelta(unique, other);
				}
				if (delta > bestDelta) {
					bestDelta = delta;
					bestColor = unique;
				}
			}
			uniqueColors.put(o, bestColor);
		}
		return uniqueColors.get(o);
	}

	private Color colorFromHash(int x) {
		return new Color(
			(x >> 24) & 0xFF,
			(x >> 16) & 0xFF,
			(x >>  8) & 0xFF,
			128
		);
	}

	private double colorDelta(Color a, Color b) {
		double dr = a.getRed()   - b.getRed();
		double dg = a.getGreen() - b.getGreen();
		double db = a.getBlue()  - b.getBlue();
		return (dr*dr) + (dg*dg) + (db*db);
	}

	/**
	* Draw a key representing the color mappings in this
	* ColorScheme at a specified location onscreen.
	* If the subject of any mappings is a Class object,
	* this method will additionally attempt to normalize
	* the class name, discarding package names and
	* outer class names.
	*
	* @param g the destination Graphics surface.
	* @param x the x-offset of the key, in pixels.
	* @param y the y-offset of the key, in pixels.
	**/
	public void drawKey(Graphics g, int x, int y) {
		Drawing.configureVector(g);
		int maxWidth = 0;
		for(Object o : uniqueColors.keySet()) {
			maxWidth = Math.max(maxWidth, Drawing.stringWidth(g, normalizedName(o)));
		}
		Drawing.drawRoundRect(
			g, x, y+7, maxWidth + 60, 24 * uniqueColors.size() + 1, 15,
			outline,
			background
		);
		y += 8;
		for(Map.Entry<Object, Color> pair : uniqueColors.entrySet()) {
			Drawing.drawRoundRect(
				g, x+5, y+4, 40, 16, 8,
				outline,
				pair.getValue()
			);
			g.drawString(normalizedName(pair.getKey()), x+50, y+16);
			y += 24;
		}
	}

	private String normalizedName(Object o) {
		if (!(o instanceof Class)) { return o.toString(); }
		Class c = (Class)o;
		String name = c.toString();
		if (name.indexOf(' ') >= 0 && name.indexOf(' ') < name.length()-1) {
			name = name.substring(name.lastIndexOf(' ')+1);
		}
		if (name.indexOf('.') >= 0 && name.indexOf('.') < name.length()-1) {
			name = name.substring(name.lastIndexOf('.')+1);
		}
		if (name.indexOf('$') >= 0 && name.indexOf('$') < name.length()-1) {
			name = name.substring(name.lastIndexOf('$')+1);
		}
		return name;
	}
}
