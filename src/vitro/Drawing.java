package vitro;

/*
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
*/
import java.awt.*;
import java.awt.geom.*;
import java.awt.Font;
import java.awt.font.*;


public class Drawing {
	
	public static int stringWidth(Graphics g, String s) {
		Font font = g.getFont();
		return (int)font.getStringBounds(s, g.getFontMetrics().getFontRenderContext()).getWidth();
	}

	public static int stringHeight(Graphics g, String s) {

		//Font font = g.getFont();
		//TextLayout layout = new TextLayout(s, font, g.getFontMetrics().getFontRenderContext());
		//Rectangle2D bounds = layout.getBounds();
		//this.base = Math.round(layout.getDescent()) + 1;

		Font font = g.getFont();
		return (int)font.getStringBounds(s, g.getFontMetrics().getFontRenderContext()).getHeight();
	}

	public static void drawStringCentered(Graphics g, String s, int x, int y) {
		if(s.length() == 0) { return; }
	
		Font font = g.getFont();
		//Rectangle2D bounds = font.getStringBounds(s, g.getFontMetrics().getFontRenderContext());

		TextLayout layout = new TextLayout(s, font, g.getFontMetrics().getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();
 
		g.drawString(
			s,
			x-(int)((bounds.getX() + bounds.getWidth())  / 2),
			y+(int)(bounds.getHeight() / 2)
		);
	}

	public static void drawCircleCentered(Graphics g, int x, int y, int radius, Color outline, Color fill) {
		g.setColor(fill);
		g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		g.setColor(outline);
		g.drawOval(x-radius, y-radius, 2*radius, 2*radius);
	}

	public static void drawRoundRect(Graphics g, int x, int y, int width, int height, int radius, Color outline, Color fill) {
		g.setColor(fill);
		g.fillRoundRect(x, y, width, height, radius, radius);
		g.setColor(outline);
		g.drawRoundRect(x, y, width, height, radius, radius);
	}
	
	public static void drawBezelRect(Graphics g, Rectangle bound, int thickness, Color ulBezel, Color drBezel, Color main) {
		g.setColor(main);
		((Graphics2D)g).fill(bound);
	
		g.setColor(ulBezel);
		for(int t = 0; t < thickness; t++) {
			g.drawLine(bound.x + t, bound.y    , bound.x                   + t, bound.y + bound.height - t);
			g.drawLine(bound.x    , bound.y + t, bound.x + bound.width - 1 - t, bound.y                + t);
		}
		
		g.setColor(drBezel);
		for(int t = 0; t < thickness; t++) {
			g.drawLine(bound.x + bound.width - t, bound.y + bound.height    , bound.x + bound.width - t, bound.y                + t);
			g.drawLine(bound.x + bound.width    , bound.y + bound.height - t, bound.x               + t, bound.y + bound.height - t);
		}
	}
	
	public static void drawBezelRect(Graphics g, int x, int y, int width, int height, int thickness, Color ulBezel, Color drBezel, Color fill) {
		drawBezelRect(g, new Rectangle(x, y, width, height), thickness, ulBezel, drBezel, fill);
	}

	public static void configureRaster(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
	}

	public static void configureVector(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}
}
