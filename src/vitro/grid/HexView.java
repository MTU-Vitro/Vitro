package vitro.grid;

import vitro.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import static vitro.util.Groups.*;

/**
* HexView is a generic View that can be applied to any Hex Model.
* It supports the display of ActorAnnotations as well as
* GridAnnotations and can be easily extended to support new
* features or alter the appearance of various elements.
*
* @author John Earnest
**/
public class HexView implements View {

	/**
	* The width of this View in pixels.
	**/
	protected final int width;
	/**
	* The height of this View in pixels.
	**/
	protected final int height;
	/**
	* The radius of a hex in pixels.
	**/
	protected final double radius;
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
	protected final Controller controller;
	/**
	* This View's ColorScheme.
	**/
	protected final ColorScheme colors;

	private Shape region;

	/**
	* Construct a new HexView.
	*
	* @param controller the Controller associated with the Model.
	* @param width the width this View should take up, in pixels.
	* @param height the height this View should take up, in pixels.
	* @param colors the ColorScheme used for drawing this View.
	**/
	public HexView(Controller controller, int width, int height, ColorScheme colors) {
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = colors;

		// calculate the size of the grid in radius units:
		double vunits = ((model().height * 2)    + (model().width > 1      ? 1 : 0  )) * Math.cos(Math.PI/6);
		double hunits =  (model().width / 2 * 3) + (model().width % 2 == 1 ? 2 : 0.5);

		// pick the smaller radius so the grid fits both axes:
		radius = Math.min(
			height * 0.8 / vunits,
			width  * 0.8 / hunits
		);

		// provide a minimum 10% padding, plus half of any remaining empty space:
		horizontalMargin = (int)((width  * 0.1) + ((width  * 0.8) - (hunits * radius)) / 2);
		  verticalMargin = (int)((height * 0.1) + ((height * 0.8) - (vunits * radius)) / 2);

		/*
		Set<Location> r = new HashSet<Location>();
		r.add(new Location(model, 2, 2));
		r.add(new Location(model, 2, 3));
		r.add(new Location(model, 2, 4));
		r.add(new Location(model, 3, 2));
		region = regionOutline(model, r, radius, radius);
		*/
	}

	/**
	* This View's Model.
	**/
	protected Hex model() { return (Hex)controller.model(); }

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
	* cell of the grid will be drawn, then Actors will
	* be drawn and finally any Annotations will be drawn.
	* This process can be customized by overriding the methods
	* responsible for each step.
	*
	* @param g the target Graphics2D surface.
	**/
	public void draw(Graphics2D g) {
		Drawing.configureVector(g);

		drawBackground((Graphics2D)g.create());

		g.translate(horizontalMargin, verticalMargin);
		for(int y = 0; y < model().height; y++) {
			for(int x = 0; x < model().width; x++) {
				drawCell((Graphics2D)g.create(), x, y);
			}
		}
		synchronized(model()) {
			for(Actor actor : model().actors) { drawActor(g, actor); }
			for(Annotation a : controller.annotations().keySet()) {
				if (a instanceof ActorAnnotation) {
					drawActorAnnotation((Graphics2D)g.create(), (ActorAnnotation)a);
				}
				else if (a instanceof GridAnnotation) {
					drawGridAnnotation((Graphics2D)g.create(),  (GridAnnotation)a);
				}
				else if (a instanceof VectorAnnotation) {
					drawVectorAnnotation(g, (VectorAnnotation)a);
				}
				else if (a instanceof GridLabelAnnotation) {
					drawGridLabelAnnotation(g, (GridLabelAnnotation)a);
				}
			}
		}

		/*
		g.setColor(Color.RED);
		g.fill(region);
		*/

		/*
		g.setColor(Color.BLUE);
		PathIterator i = region.getPathIterator(null);
		int x = 0;
		while(!i.isDone()) {
			double[] coords = new double[6];
			i.currentSegment(coords);
			g.drawString(""+x, (int)(coords[0]), (int)(coords[1]));
			i.next();
			x++;
		}
		*/

		g.translate(-horizontalMargin, -verticalMargin);
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
	* Render one cell of the grid.
	*
	* @param g the target Graphics2D surface.
	* @param x the x-coordinate of this cell in cells.
	* @param y the y-coordinate of this cell in cells.
	**/
	protected void drawCell(Graphics2D g, int x, int y) {
		g.setColor(colors.outline);
		g.draw(hexOutline(new Location(model(), x, y), radius, radius));
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
		if (a instanceof Factional) {
			g.setColor(colors.unique(new Integer(((Factional)a).team())));
		}
		else {
			g.setColor(colors.unique(a.getClass()));
		}

		Point2D.Double center = hexCenter(location, radius);
		Shape outline = new Ellipse2D.Double(
			center.x - radius * 0.5,
			center.y - radius * 0.5,
			radius,
			radius
		);

		g.fill(outline);
		g.setColor(colors.outline);
		g.draw(outline);
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

		g.setStroke(new BasicStroke(
			2,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND,
			0,
			new float[] {4, 4},
			0
		));
		g.setColor(colors.unique(a.label));
		Point2D.Double center = hexCenter(location, radius);
		Shape outline = new Ellipse2D.Double(
			center.x - radius * 0.6,
			center.y - radius * 0.6,
			radius * 1.2,
			radius * 1.2
		);
		g.draw(outline);
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
				g.draw(hexOutline(location, radius, radius));
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
			Point2D.Double center = hexCenter(e.getKey(), radius);
			Drawing.drawStringCentered(
				g,
				e.getValue(),
				(int)center.x,
				(int)center.y
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
		for(Map.Entry<Location, Double> e : a.dirs.entrySet()) {
			drawVector((Graphics2D)gc.create(), e.getKey(), e.getValue());
		}
	}

	/**
	* Render a single vector, as from a VectorAnnotation.
	*
	* @param cell the target cell.
	* @param dir the direction of this vector.
	**/
	protected void drawVector(Graphics2D g, Location cell, double dir) {
		Point2D.Double center = hexCenter(cell, radius);
		g.translate(center.x, center.y);
		g.rotate(2 * Math.PI * dir);

		int r = (int)(radius * .4 );
		int w = (int)(radius * .15);

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

	/**
	* Calculate the centerpoint of a cell within a hexgrid,
	* given hexes of a specific radius.
	*
	* @param src the grid coordinates of the cell to consider.
	* @param radius the radius of each hex, in pixels.
	* @return drawing coordinates for the hexagon's center.
	**/
	public static Point2D.Double hexCenter(Location src, double radius) {
		final double vspacing = Math.cos(Math.PI / 6) * radius;
		final double hspacing = Math.sin(Math.PI / 6) * radius;
		final double cx = (2 * hspacing) + src.x * (hspacing + radius);
		final double cy = (src.y * 2 * vspacing) + ((src.x % 2) * vspacing) + vspacing;

		return new Point2D.Double(cx, cy);
	}

	/**
	* Calculate the position of a corner of a hex cell.
	* For a given direction, hexCorner will calculate the position
	* of the corner on the counter-clockwise-most end of that face.
	*
	* @param src the grid coordinates of the cell to consider.
	* @param direction the corner to consider
	* @param gridRadius the radius of hexagons on the grid.
	* @param hexRadius the radius for the hexagon being calculated.
	* @return drawing coordinates for the hexagon's corner.
	**/
	public static Point2D.Double hexCorner(Location src, Hex.Dir direction, double gridRadius, double hexRadius) {
		int dir;
		switch(direction) {
			case N  : dir = 4; break;
			case NE : dir = 5; break;
			case SE : dir = 0; break;
			case S  : dir = 1; break;
			case SW : dir = 2; break;
			case NW : dir = 3; break;

			default: throw new Error("Unknown direction!");
		}
		Point2D.Double center = hexCenter(src, gridRadius);
		return new Point2D.Double(
			center.x + hexRadius * Math.cos(Math.PI / 3 * dir),
			center.y + hexRadius * Math.sin(Math.PI / 3 * dir)
		);
	}

	private static int mod(int a, int b) {
		a %= b;
		return a < 0 ? a+b : a;
	}

	private static Hex.Dir dir(int x) {
		switch(mod(x, 6)) {
			case  0 : return Hex.Dir.N;
			case  1 : return Hex.Dir.NE;
			case  2 : return Hex.Dir.SE;
			case  3 : return Hex.Dir.S;
			case  4 : return Hex.Dir.SW;
			default : return Hex.Dir.NW;
		}
	}

	/**
	* Calculate a Shape representing the outline
	* of a single Hexagon.
	*
	* @param src grid coordinates of the cell to consider.
	* @param gridRadius the radius of hexagons on the grid.
	* @param hexRadius the radius for the hexagon being calculated.
	* @return shape representing the specified hex cell.
	**/
	public static Shape hexOutline(Location src, double gridRadius, double hexRadius) {
		Path2D.Double ret = new Path2D.Double();
		Point2D.Double start = hexCorner(src, Hex.Dir.N, gridRadius, hexRadius);
		ret.moveTo(start.x, start.y);
		for(int x = 1; x <= 6; x++) {
			Point2D.Double next = hexCorner(src, dir(x), gridRadius, hexRadius);
			ret.lineTo(next.x, next.y);
		}
		return ret;
	}

	private static Shape regionOutline(Hex grid, Set<Location> region, double gridRadius, double hexRadius) {
		Location leader = any(region);
		while(region.contains(grid.neighbor(leader, dir(0)))) {
			leader = grid.neighbor(leader, dir(0));
		}

		Location here = leader;
		Path2D.Double  ret = new Path2D.Double();
		Point2D.Double src = hexCorner(here, dir(0), gridRadius, hexRadius);
		ret.moveTo(src.x, src.y);

		int dir = 1;
		while(true) {
			Point2D.Double next = hexCorner(here, dir(dir), gridRadius, hexRadius);
			//System.out.format("corner loc:%s dir:%d%n", here, dir);

			ret.lineTo(next.x, next.y);
			if (!region.contains(grid.neighbor(here, dir(dir)))) {
				dir = mod(dir + 1, 6);
			}
			else {
				here = grid.neighbor(here, dir(dir));
				Point2D.Double innerCorner = hexCorner(here, dir(dir+4), gridRadius, hexRadius);
				ret.lineTo(innerCorner.x, innerCorner.y);
				dir = mod(dir - 1, 6);
			}
			if (leader.equals(here) && (dir == 0)) { return ret; }
		}
	}
}
