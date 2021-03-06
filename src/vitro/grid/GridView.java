package vitro.grid;

import vitro.*;
import java.awt.*;
import static vitro.util.Groups.*;

/**
* GridView is a generic View that can be applied to any GridModel.
* It supports the display of ActorAnnotations as well as
* GridAnnotations and can be easily extended to support new
* features or alter the appearance of various elements.
*
* @author John Earnest
**/
public class GridView implements View {

	/**
	* The width of this View in pixels.
	**/
	protected final int width;
	/**
	* The height of this View in pixels.
	**/
	protected final int height;
	/**
	* The size (width and height) of a cell in pixels.
	**/
	protected final int cellSize;
	/**
	* The margin between the edges of a cell and an Actor.
	**/
	protected int cellMargin;
	/**
	* The x-offset of the grid relative to the View.
	**/
	protected int horizontalMargin;
	/**
	* The y-offset of the grid relative to the View.
	**/
	protected int verticalMargin;

	/**
	* This View's Model.
	**/
	protected final Grid        model;
	/**
	* This View's Controller.
	**/
	protected final Controller  controller;
	/**
	* This View's ColorScheme.
	**/
	protected final ColorScheme colors;

	/**
	* Construct a new GridView.
	*
	* @param model the Model this View will visualize.
	* @param controller the Controller associated with the Model.
	* @param width the width this View should take up, in pixels.
	* @param height the height this View should take up, in pixels.
	* @param colors the ColorScheme used for drawing this View.
	**/
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

	/**
	* {@inheritDoc}
	**/
	public Controller  controller()  { return controller; }
	/**
	* {@inheritDoc}
	**/
	public ColorScheme colorScheme() { return colors;     }
	/**
	* {@inheritDoc}
	**/
	public int         width()       { return width;      }
	/**
	* {@inheritDoc}
	**/
	public int         height()      { return height;     }

	/**
	* Render the entire View.
	* First, the background will be drawn, then every
	* cell of the Grid will be drawn, then Actors will
	* be drawn and finally any Annotations will be drawn.
	* This process can be customized by overriding the methods
	* responsible for each step.
	*
	* @param g the target Graphics2D surface.
	**/
	public void draw(Graphics2D g) {
		drawBackground(g);
		g.fillRect(0, 0, width, height);
		Drawing.configureVector(g);

		for(int y = 0; y < model.height; y++) {
			for(int x = 0; x < model.width; x++) {
				drawCell(g, x, y);
			}
		}
		synchronized(model) {
			for(Actor actor : model.actors) { drawActor(g, actor); }
			for(Annotation a : controller.annotations().keySet()) {
				if (a instanceof ActorAnnotation) {
					drawActorAnnotation(g, (ActorAnnotation)a);
				}
				else if (a instanceof GridAnnotation) {
					drawGridAnnotation(g, (GridAnnotation)a);
				}
			}
		}
	}

	/**
	* Render the background of this View.
	*
	* @param g the target Graphics2D surface.
	**/
	protected void drawBackground(Graphics2D g) {
		g.setColor(colors.background);
	}

	/**
	* Render one cell of the Grid, starting at a specified position.
	*
	* @param g the target Graphics2D surface.
	* @param x the x-coordinate of the top-left corner of this cell in pixels.
	* @param y the y-coordinate of the top-left corner of this cell in pixels.
	**/
	protected void drawCell(Graphics2D g, int x, int y) {
		g.setColor(colors.outline);
		g.drawRect(
			horizontalMargin + (x * cellSize),
			verticalMargin   + (y * cellSize),
			cellSize,
			cellSize
		);
	}

	/**
	* Render one Actor.
	*
	* @param g the target Graphics2D surface.
	* @param a the Actor to render.
	**/
	protected void drawActor(Graphics2D g, Actor a) {
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

	/**
	* Render one ActorAnnotation.
	*
	* @param g the target Graphics2D surface.
	* @param a the ActorAnnotation to render.
	**/
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
	
	/**
	* Render one GridAnnotation.
	*
	* @param g the target Graphics2D surface.
	* @param a the GridAnnotation to render.
	**/
	protected void drawGridAnnotation(Graphics2D g, GridAnnotation a) {
		for(Point p : a.coloring.keySet()) {
			g.setColor(a.coloring.get(p));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			
			Location location = new Location(model, p.x, p.y);
			if(location.valid()) {
				g.fillRect(
					horizontalMargin + (location.x * cellSize),
					verticalMargin   + (location.y * cellSize),
					cellSize,
					cellSize
				);
			}
		}
	}

	private double sofar = 0;
	/**
	* {@inheritDoc}
	**/
	public void tick(double time) {
		sofar += time;
		if (sofar > .05) {
			controller.next();
			sofar = 0;
		}
	}

	/**
	* {@inheritDoc}
	**/
	public void flush() {
		
	}

}
