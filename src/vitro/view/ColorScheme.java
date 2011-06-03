package vitro.view;

import java.awt.Graphics;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class ColorScheme {

	public Color outline;
	public Color secondary;
	public Color background;

	// heading
	// label

	public ColorScheme() {
		this(
			Color.BLACK,
			Color.LIGHT_GRAY,
			Color.WHITE
		);
	}

	public ColorScheme(Color outline, Color secondary, Color background) {
		this.outline    = outline;
		this.secondary  = secondary;
		this.background = background;
	}

	public final Map<Object, Color> uniqueColors = new HashMap<Object, Color>();

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

	public void drawKey(Graphics g, int x, int y) {
		int maxWidth = 0;
		for(Object o : uniqueColors.keySet()) {
			maxWidth = Math.max(maxWidth, Drawing.stringWidth(g, normalizedName(o)));
		}
		Drawing.drawRoundRect(
			g, x, y+7, maxWidth + 60, 24 * uniqueColors.size() + 1, 15,
			outline,
			background
		);
		g.drawString("Key:", x+3, 18);
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