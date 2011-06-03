package vitro.view;

import java.awt.Color;

public class ColorScheme {

	public Color outline;
	public Color secondary;
	public Color background;

	public ColorScheme() {
		this(
			Color.BLACK,
			Color.GRAY,
			Color.WHITE
		);
	}

	public ColorScheme(Color outline, Color secondary, Color background) {
		this.outline    = outline;
		this.secondary  = secondary;
		this.background = background;
	}

}