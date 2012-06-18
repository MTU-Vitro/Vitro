package demos.lights;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class LightsOutView extends GridView {
	
	public LightsOutView(Controller controller, int width, int height) {
		super(controller, width, height, new ColorScheme(Color.BLACK, Color.LIGHT_GRAY, Color.BLUE));
		colors.inactive = Color.GRAY;
		colors.setColor(new Integer(0), new Color(141, 119, 197));
		colors.setColor(new Integer(1), new Color(255, 133, 242));
	}

	protected void drawBackground(Graphics2D g) {
		g.setPaint(new LinearGradientPaint(
			new Point2D.Double(0,      0),
			new Point2D.Double(0, height),
			new float[] {0.0f, .50f, 1.0f},
			new Color[] {
				colors.background.darker().darker(),
				colors.background.darker(),
				colors.background,
			}
		));
	
		g.fillRect(0, 0, width, height);
		g.setPaint(null);
	}

	protected void drawCell(Graphics2D g, int x, int y) {
		// we don't need to draw the grid background,
		// so stub out this method.
	}

	protected void drawActor(Graphics2D g, Actor a) {
		Location location = model().locations.get(a);
		if (location == null) { return; }

		int cx = horizontalMargin + cellMargin + (location.x * cellSize) + (cellSize / 2);
		int cy = verticalMargin   + cellMargin + (location.y * cellSize) + (cellSize / 2);
		int w  = cellSize - (cellMargin * 2);
		int h  = cellSize - (cellMargin * 2);

		if (a instanceof Factional) {
			int team = ((Factional)a).team();
			Color color = colors.unique(new Integer(team));
			g.setColor(color);

			if (team != 0) {
				g.setPaint(new RadialGradientPaint(
					new Point2D.Double(cx, cy),
					Math.min(w, h)/2,
					new float[] {0.0f, .8f, 1.0f},
					new Color[] {
						color.brighter(),
						color,
						color.darker()
					}
				));
				g.fill(roundRect(cx, cy, w+1, h+1, 2));
				color = trans(color.darker(), 150);
			}

			g.setPaint(new LinearGradientPaint(
				new Point2D.Double(cx, cy-(h/2)),
				new Point2D.Double(cx, cy+(h/2)),
				new float[] {0.0f, .25f, .75f, 1.0f},
				new Color[] {
					color.darker(),
					color,
					color,
					color.brighter()
				},
				MultipleGradientPaint.CycleMethod.REPEAT
			));
		}
		else {
			g.setColor(colors.unique(a.getClass()));
		}
		g.fill(roundRect(cx, cy, w+1, h+1, 2));

		g.setPaint(null);
		g.setColor(colors.outline);
		g.draw(roundRect(cx, cy, w, h, 4));
	}

	private Shape roundRect(int cx, int cy, double w, double h, double corner) {
		return new RoundRectangle2D.Double(
			cx - w/2,
			cy - h/2,
			w,
			h,
			corner,
			corner
		);
	}

	private Color trans(Color color, int alpha) {
		return new Color(
			color.getRed(),
			color.getBlue(),
			color.getGreen(),
			alpha
		);
	}
}
