package demos;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import static vitro.util.Groups.*;

public class GridView implements View {

	protected final int width;
	protected final int height;
	protected final int cellSize;
	protected final int cellMargin;
	protected final int horizontalMargin;
	protected final int verticalMargin;

	protected final Grid        model;
	protected final Controller  controller;
	protected final ColorScheme colors;

	public GridView(Grid model, Controller controller, int width, int height, ColorScheme colors) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = colors;

		cellSize = (int)Math.min(
			width  * .8 / model.width,
			height * .8 / model.height
		);
		horizontalMargin = (width  - (model.width  * cellSize)) / 2;
		verticalMargin   = (height - (model.height * cellSize)) / 2;
		cellMargin       = cellSize / 10;
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	public void draw(Graphics g) {
		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);
		Drawing.configureVector(g);

		for(int y = 0; y < model.height; y++) {
			for(int x = 0; x < model.height; x++) {
				g.setColor(colors.outline);
				g.drawRect(
					horizontalMargin + (x * cellSize),
					verticalMargin   + (y * cellSize),
					cellSize,
					cellSize
				);
			}
		}
		synchronized(model) {
			for(Actor actor : model.actors) { drawActor(g, actor); }
			for(Annotation a : controller.annotations().keySet()) {
				if (a instanceof ActorAnnotation) {
					drawActorAnnotation((Graphics2D)g, (ActorAnnotation)a);
				}
			}
		}
	}

	protected void drawActor(Graphics g, Actor a) {
		Location location = model.locations.get(a);
		if (location == null) { return; }
		if (a instanceof Factional) {
			g.setColor(colors.unique(new Integer(((Factional)a).team())));
		}
		else {
			g.setColor(colors.unique(a.getClass()));
		}
		g.fillOval(
			horizontalMargin + cellMargin + (location.x * cellSize) + 1,
			verticalMargin   + cellMargin + (location.y * cellSize) + 1,
			cellSize - (cellMargin * 2) - 1,
			cellSize - (cellMargin * 2)
		);
		g.setColor(colors.outline);
		g.drawOval(
			horizontalMargin + cellMargin + (location.x * cellSize) + 1,
			verticalMargin   + cellMargin + (location.y * cellSize) + 1,
			cellSize - (cellMargin * 2) - 1,
			cellSize - (cellMargin * 2)
		);
	}

	protected void drawActorAnnotation(Graphics2D g, ActorAnnotation a) {
		Location location = model.locations.get(a.actor);
		if (location == null) { return; }

		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(
			2,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND,
			0,
			new float[] {4, 4},
			0
		));
		g.setColor(colors.unique(a.label));
		g.drawOval(
			horizontalMargin + cellMargin/2 + (location.x * cellSize),
			verticalMargin   + cellMargin/2 + (location.y * cellSize),
			cellSize - cellMargin,
			cellSize - cellMargin
		);
		g.setStroke(oldStroke);
	}

	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if (sofar > .75) {
			controller.next();
			sofar = 0;
		}
	}

	public void flush() {
		
	}

}
