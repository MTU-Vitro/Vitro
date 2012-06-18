package demos;

import java.awt.*;

public class RasterFont {
	public final int characterWidth;
	public final int characterHeight;
	public final Image tiles;

	public RasterFont(int charWidth, int charHeight, Image tiles) {
		this.characterWidth  = charWidth;
		this.characterHeight = charHeight;
		this.tiles = tiles;
	}

	public void draw(Graphics g, int x, int y, String text) {
		for(char c : text.toCharArray()) {
			drawChar(x, y, c, g);
			x += characterWidth;
		}
	}

	public int length(String text) {
		return text.length() * characterWidth;
	}

	private void drawChar(int x, int y, char c, Graphics g) {
		int tx = ((c - 32) % 16) * characterWidth;
		int ty = ((c - 32) / 16) * characterHeight;
		g.drawImage(
			tiles,
			x,
			y,
			x + characterWidth,
			y + characterHeight,
			tx,
			ty,
			tx + characterWidth,
			ty + characterHeight,
			null
		);
	}
}
