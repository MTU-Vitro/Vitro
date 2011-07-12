package demos;

import java.awt.*;
import java.util.*;

public class VectorFont {
	public final int characterWidth;
	public final int characterHeight;

	private static final int spacing = 4;
	private final int charWidth;
	private final int charHeight;

	public VectorFont(int charWidth, int charHeight) {
		this.characterWidth  = charWidth;
		this.characterHeight = charHeight;

		this.charWidth  = charWidth;
		this.charHeight = charHeight;
	}

	public void draw(Graphics g, int x, int y, String text) {
		for(char c : text.toCharArray()) {
			Glyph glyph = letters.containsKey(c) ? letters.get(c) : def;
			glyph.draw(g, x, y, charWidth, charHeight);
			x += charWidth + spacing;
		}
	}

	public int length(String text) {
		int length = 0;
		for(char c : text.toCharArray()) {
			length += charWidth + spacing;
		}
		return length - spacing;
	}

	private static class Glyph {
		private final double[] coords;

		public Glyph(double... coords) {
			this.coords = coords;
		}

		public void draw(Graphics g, int x, int y, double sx, double sy) {
			for(int i = 0; i < coords.length; i += 4) {
				g.drawLine(
					x + (int)(sx * coords[i  ]),
					y + (int)(sy * coords[i+1]),
					x + (int)(sx * coords[i+2]),
					y + (int)(sy * coords[i+3])
				);
			}
		}
	}

	private static final Glyph def = new Glyph(
		0, 0, 0, 1,
		0, 1, 1, 1,
		1, 1, 1, 0,
		1, 0, 0, 0,
		0, 0, 1, 1,
		1, 0, 0, 1
	);

	private static final Map<Character, Glyph> letters = new HashMap<Character, Glyph>();
	{
		letters.put(' ', new Glyph());
		letters.put('A', new Glyph(
			.5,  0,  0, .3,
			.5,  0,  1, .3,
			 0, .3,  1, .3,
			 0, .3,  0,  1,
			 1, .3,  1,  1
		));
		letters.put('B', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0,
			1, 0, 0, 0,
			0, .3, 1, .3
		));
		letters.put('C', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 0, 0, 0
		));
		letters.put('D', new Glyph(
			0, 0, 0, 1,
			0, 0, .6, 0,
			0, 1, .6, 1,
			.6, 0, 1, .3,
			.6, 1, 1, .6,
			1, .3, 1, .6
		));
		letters.put('E', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 0, 0, 0,
			0, .3, .6, .3
		));
		letters.put('F', new Glyph(
			0, 0, 0, 1,
			1, 0, 0, 0,
			0, .3, .6, .3
		));
		letters.put('G', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 0, 0, 0,
			1, 1, 1, .3,
			1, .3, .3, .3
		));
		letters.put('H', new Glyph(
			0, 0, 0, 1,
			0, 1, 0, 1,
			1, 1, 1, 0,
			0, .3, 1, .3
		));
		letters.put('I', new Glyph(
			0, 0, 1, 0,
			0, 1, 1, 1,
			.5, 0, .5, 1
		));
		letters.put('J', new Glyph(
			0, 0, 1, 0,
			0, 1, .5, 1,
			.5, 0, .5, 1
		));
		letters.put('K', new Glyph(
			0, 0, 0, 1,
			0, .3, 1, 0,
			0, .3, 1, 1
		));
		letters.put('L', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1
		));
		letters.put('M', new Glyph(
			0, 0, 0, 1,
			1, 0, 1, 1,
			0, 0, .5, .3,
			1, 0, .5, .3
		));
		letters.put('N', new Glyph(
			0, 0, 0, 1,
			1, 0, 1, 1,
			0, 0, 1, 1
		));
		letters.put('O', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0,
			1, 0, 0, 0
		));
		letters.put('P', new Glyph(
			0, 0, 0, 1,
			0, 0, 1, 0,
			0, .3, 1, .3,
			1, 0, 1, .3
		));
		letters.put('Q', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0,
			1, 0, 0, 0,
			1, 1, .5, .5
		));
		letters.put('R', new Glyph(
			0, 0, 0, 1,
			0, 0, 1, 0,
			0, .3, 1, .3,
			1, 0, 1, .3,
			0, .3, 1, 1
		));
		letters.put('S', new Glyph(
			0, 1, 1, 1,
			1, 1, 1, .3,
			0, .3, 1, .3,
			0, 0, 0, .3,
			0, 0, 1, 0
		));
		letters.put('T', new Glyph(
			0, 0, 1, 0,
			.5, 0, .5, 1
		));
		letters.put('U', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0
		));
		letters.put('V', new Glyph(
			0, 0, .5, 1,
			1, 0, .5, 1
		));
		letters.put('W', new Glyph(
			0, 0, 0, 1,
			1, 0, 1, 1,
			0, 1, .5, .6,
			1, 1, .5, .6
		));
		letters.put('X', new Glyph(
			0, 0, 1, 1,
			1, 0, 0, 1
		));
		letters.put('Y', new Glyph(
			0, 0, .5, .3,
			1, 0, .5, .3,
			.5, .3, .5, 1
		));
		letters.put('Z', new Glyph(
			0, 0, 1, 0,
			0, 1, 1, 1,
			1, 0, 0, 1
		));
		letters.put('1', new Glyph(
			1, 0, 1, 1
		));
		letters.put('2', new Glyph(
			0, 0, 1, 0,
			1, 0, 1, .5,
			0, .5, 1, .5,
			0, .5, 0, 1,
			0, 1, 1, 1
		));
		letters.put('3', new Glyph(
			0, 0, 1, 0,
			1, 0, 1, 1,
			0, 1, 1, 1,
			0, .5, 1, .5
		));
		letters.put('4', new Glyph(
			0, 0, 0, .5,
			1, 0, 1, 1,
			0, .5, 1, .5
		));
		letters.put('5', new Glyph(
			0, 1, 1, 1,
			1, 1, 1, .5,
			0, .5, 1, .5,
			0, 0, 0, .5,
			0, 0, 1, 0
		));
		letters.put('6', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			0, .5, 1, .5,
			1, 1, 1, .5
		));
		letters.put('7', new Glyph(
			0, 0, 1, 0,
			1, 0, 1, 1
		));
		letters.put('8', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0,
			1, 0, 0, 0,
			0, .5, 1, .5
		));
		letters.put('9', new Glyph(
			0, 0, 1, 0,
			1, 0, 1, 1,
			0, 0, 0, .5,
			0, .5, 1, .5
		));
		letters.put('0', new Glyph(
			0, 0, 0, 1,
			0, 1, 1, 1,
			1, 1, 1, 0,
			1, 0, 0, 0,
			1, 0, 0, 1
		));
		letters.put('-', new Glyph(
			0, .5, 1, .5
		));
		letters.put(':', new Glyph(
			.14, .14, .42, .14,
			.14, .14, .14, .42,
			.14, .42, .42, .42,
			.42, .14, .42, .42,

			.14, .56, .42, .56,
			.14, .56, .14, .84,
			.42, .56, .42, .84,
			.14, .84, .42, .84
		));
		letters.put('.', new Glyph(
			.25, .56, .75, .56,
			.25, .56, .25, .84,
			.75, .56, .75, .84,
			.25, .84, .75, .84
		));
		letters.put('/', new Glyph(
			0, 1, 1, 0
		));
		letters.put('[', new Glyph(
			1.0, 0.1, 0.5, 0.1,
			0.5, 0.1, 0.5, 0.9,
			0.5, 0.9, 1.0, 0.9
		));
		letters.put(']', new Glyph(
			0.0, 0.1, 0.5, 0.1,
			0.5, 0.1, 0.5, 0.9,
			0.5, 0.9, 0.0, 0.9
		));
	}
}
