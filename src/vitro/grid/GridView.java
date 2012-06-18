package vitro.grid;

import vitro.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import static vitro.util.Groups.*;

/**
* GridView is a generic View that can be applied to any GridModel.
* It supports the display of ActorAnnotations as well as
* GridAnnotations and GridLabelAnnotations, and can be easily
* extended to support new features or alter the appearance of
* various elements.
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
	* @param controller the Controller associated with the Model.
	* @param width the width this View should take up, in pixels.
	* @param height the height this View should take up, in pixels.
	* @param colors the ColorScheme used for drawing this View.
	**/
	public GridView(Controller controller, int width, int height, ColorScheme colors) {
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = colors;

		cellSize = (int)Math.min(
			width  * .8 / model().width,
			height * .8 / model().height
		);
		horizontalMargin = (width  - (model().width  * cellSize)) / 2;
		verticalMargin   = (height - (model().height * cellSize)) / 2;
		cellMargin       = cellSize / 10;
	}

	/**
	* This View's Model.
	**/
	protected Grid model() { return (Grid)controller.model(); }

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
		Drawing.configureVector(g);

		drawBackground(g);

		for(int y = 0; y < model().height; y++) {
			for(int x = 0; x < model().width; x++) {
				drawCell(g, x, y);
			}
		}
		synchronized(controller) {
			for(Actor actor : model().actors) { drawActor(g, actor); }
			for(Annotation a : controller.annotations().keySet()) {
				if (a instanceof ActorAnnotation) {
					drawActorAnnotation(g, (ActorAnnotation)a);
				}
				else if (a instanceof GridAnnotation) {
					drawGridAnnotation(g, (GridAnnotation)a);
				}
				else if (a instanceof VectorAnnotation) {
					drawVectorAnnotation(g, (VectorAnnotation)a);
				}
				else if (a instanceof GridLabelAnnotation) {
					drawGridLabelAnnotation(g, (GridLabelAnnotation)a);
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
		g.fillRect(0, 0, width, height);
	}

	/**
	* Render one cell of the Grid.
	*
	* @param g the target Graphics2D surface.
	* @param x the x-coordinate of this cell in cells.
	* @param y the y-coordinate of this cell in cells.
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
		Location location = model().locations.get(a);
		if (location == null) { return; }

		double cx = horizontalMargin + (location.x * cellSize) + (cellSize / 2.0);
		double cy = verticalMargin   + (location.y * cellSize) + (cellSize / 2.0);
		double r  = cellSize * .3;
		Ellipse2D.Double oval = new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r);

		g.setColor(colors.unique(
			a instanceof Factional ?
			new Integer(((Factional)a).team()) :
			a.getClass()
		));

		g.fill(oval);
		g.setColor(colors.outline);
		g.draw(oval);
	}

	/**
	* Render one ActorAnnotation.
	*
	* @param g the target Graphics2D surface.
	* @param a the ActorAnnotation to render.
	**/
	protected void drawActorAnnotation(Graphics2D g, ActorAnnotation a) {
		Location location = model().locations.get(a.actor);
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
			
			Location location = new Location(model(), p.x, p.y);
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

	/**
	* Render one GridLabelAnnotation.
	*
	* @param g the target Graphics2D surface.
	* @param a the GridLabelAnnotation to render.
	**/
	protected void drawGridLabelAnnotation(Graphics2D g, GridLabelAnnotation a) {
		g.setColor(colors.outline);
		for(Map.Entry<Location, String> e : a.labels.entrySet()) {
			Drawing.drawStringCentered(
				g,
				e.getValue(),
				horizontalMargin + (e.getKey().x * cellSize) + (cellSize / 2),
				verticalMargin   + (e.getKey().y * cellSize) + (cellSize / 2)
			);
		}
	}

	/**
	* Render one VectorAnnotation.
	*
	* @param g the target Graphics2D surface.
	* @param a the VectorAnnotation to render.
	**/
	protected void drawVectorAnnotation(Graphics2D g, VectorAnnotation a) {
		Graphics2D gc = (Graphics2D)g.create();
		gc.setColor(colors.outline);
		gc.setStroke(new BasicStroke(2));
		for(Map.Entry<Location, Integer> e : a.dirs.entrySet()) {
			drawVector((Graphics2D)gc.create(), e.getKey(), e.getValue());
		}
	}

	/**
	* Render a single vector, as from a VectorAnnotation.
	*
	* @param cell the target cell.
	* @param dir the direction of this vector.
	**/
	protected void drawVector(Graphics2D g, Location cell, int dir) {
		g.translate(
			horizontalMargin + (cell.x * cellSize) + (cellSize / 2.0),
			verticalMargin   + (cell.y * cellSize) + (cellSize / 2.0)
		);
		g.rotate(Math.PI / 4 * dir);

		int r = (int)(cellSize * .4);
		int w = (int)(cellSize * .15);

		g.drawLine( 0,  -r, 0, r);
		g.drawLine(-w, r/2, 0, r);
		g.drawLine( w, r/2, 0, r);
	}

	private double sofar = 0;
	/**
	* {@inheritDoc}
	**/
	public void tick(double time) {
		sofar += time;
		if (sofar > .5) {
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
