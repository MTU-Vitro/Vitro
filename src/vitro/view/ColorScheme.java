package vitro.view;

import java.awt.Graphics;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;

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
			int x = o.hashCode();
			Color unique = new Color(
				((x >> 24) & 0xF0) | ((x >>  0) & 0x0F),
				((x >> 16) & 0xF0) | ((x >>  8) & 0x0F),
				((x >>  8) & 0xF0) | ((x >> 16) & 0x0F),
				128
			);
			uniqueColors.put(o, unique);
		}
		return uniqueColors.get(o);
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
		if (name.indexOf('$') >= 0 && name.indexOf('$') < name.length()-1) {
			name = name.substring(name.lastIndexOf('$')+1);
		}
		return name;
	}
}