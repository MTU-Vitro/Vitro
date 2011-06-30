package demos;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.awt.*;
import java.util.*;
import static vitro.util.Groups.*;

public class LunarView implements View {

	private final int width;
	private final int height;

	private final LunarWorld model;
	private final Controller controller;
	private final ColorScheme colors;
	
	private final LanderView landerView;
	private final Set<Point> stars;
	private final Polygon mountains;
	private final Polygon ground;
	private final VectorFont font;

	private Polygon blast = null;

	public LunarView(LunarWorld model, Controller controller, int width, int height) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = new ColorScheme(Color.WHITE, new Color(75, 75, 75), Color.BLACK);
		
		landerView = new LanderView(model.lander);
		font = new VectorFont(12, 17);

		stars = new HashSet<Point>();
		for(int x = 0; x < 40; x++) {
			stars.add(new Point(
				(int)(Math.random() * width),
				(int)(Math.random() * height)
			));
		}

		mountains = buildMountains(width, height, height/2, 20);
		ground    = buildGround(width, height, (int)model.positions.get(model.target).y);
	}

	private Polygon buildMountains(int width, int height, int starty, int heightchange) {
		int[] x = new int[50];
		int[] y = new int[50];
		int xp = -10;
		int yp = starty;
		for(int i = 1; i < 49; i++) {
			x[i] = xp;
			y[i] = yp;
			xp += (int)(Math.random() * 10) + (width / 40);
			yp += (int)(Math.random() * (2*heightchange)) - heightchange;
		}
		x[ 0] = -20;       y[ 0] = 2 * height;
		x[49] = 2 * width; y[49] = 2 * height;
		return new Polygon(x, y, 50);
	}

	private Polygon buildGround(int width, int height, int starty) {
		int[] x = new int[50];
		int[] y = new int[50];
		int xp = -10;
		for(int i = 1; i < 49; i++) {
			x[i] = xp;
			y[i] = starty + (int)(Math.random() *  6) - 3;
			xp +=           (int)(Math.random() * 10) + (width / 40);
		}
		x[ 0] = -20;       y[ 0] = 2 * height;
		x[49] = 2 * width; y[49] = 2 * height;
		return new Polygon(x, y, 50);
	}

	private Polygon blast(int sx, int sy) {
		int[] x = new int[20];
		int[] y = new int[20];
		for(int i = 0; i < x.length; i++) {
			double r = Math.PI * 2 * ((double)i / x.length);
			x[i] = sx + (int)(Math.cos(r) * ((Math.random() * 20) + 25));
			y[i] = sy + (int)(Math.sin(r) * ((Math.random() * 20) + 25));
		}
		return new Polygon(x, y, 20);
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	public void draw(Graphics g) {
		Drawing.configureVector(g);

		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);

		g.setColor(colors.outline);
		for(Point star : stars) {
			g.drawLine(star.x, star.y, star.x, star.y);
		}

		g.setColor(colors.background);
		g.fillPolygon(mountains);
		g.setColor(new Color(50, 50, 50));
		g.drawPolygon(mountains);
		if (blast != null) {
			g.setColor(colors.outline);
			g.fillPolygon(blast);
			//g.setColor(colors.outline);
			//g.drawPolygon(blast);
		}
		g.setColor(colors.background);
		g.fillPolygon(ground);
		g.setColor(colors.outline);
		g.drawPolygon(ground);

		synchronized(model) {

			AlignedBox bound = model.target.bound();
			Rectangle target = new Rectangle(
				(int)bound.point0.x,
				(int)bound.point0.y,
				(int)(bound.point1.x - bound.point0.x),
				(int)(bound.point1.y - bound.point0.y)
			);
			g.setColor(colors.background);
			((Graphics2D)g).fill(target);
			g.setColor(colors.outline);
			((Graphics2D)g).draw(target);

			Position lander = model.positions.get(model.lander);
			landerView.draw(
				(Graphics2D)g,
				(int)lander.x,
				(int)lander.y,
				model.success()
			);
			if (model.done() & !model.success()) {
				blast = blast(
					(int)lander.x,
					(int)lander.y
				);
			}

			g.setColor(colors.outline);
			font.draw(g, 10, 10, String.format("VX:  % 07.2f", model.lander.velocity.x));
			font.draw(g, 10, 30, String.format("VY:  % 07.2f", model.lander.velocity.y));
			font.draw(g, 10, 50, String.format("FUEL:% 4d",    model.lander.fuel));

			double alt  = model.positions.get(model.target).y - model.positions.get(model.lander).y;
			double dist = model.positions.get(model.target).x - model.positions.get(model.lander).x;

			font.draw(g, 10, 70, String.format("ALTITUDE:% 07.2f", alt));
			font.draw(g, 10, 90, String.format("DISTANCE:% 07.2f", dist));

		}
	}

	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if(sofar > .10) {
			controller.next();
			sofar = 0;
		}
	}

	public void flush() {

	}

	public class LanderView {
		public final LunarWorld.LunarLander lander;
		
		public LanderView(LunarWorld.LunarLander lander) {
			this.lander = lander;
		}

		protected void draw(Graphics2D g, int x, int y, boolean success) {
			g.setColor(Color.WHITE);
			LunarWorld.ThrusterAction lastThrust = null;
			
			if(controller.hasPrev()) {
				java.util.List<Action> actions = controller().previousActions();
				lastThrust = (LunarWorld.ThrusterAction)Groups.firstOfType(LunarWorld.ThrusterAction.class, actions);
			}
			if(lastThrust != null) {
				if(lastThrust.mThrusterFired) {
					int  h = (int)(Math.random() * 10) + 20;
					int dx = (int)(Math.random() *  2)  - 1;
					g.drawLine(x - 6, y + 10, x + dx, y + 10 + h);
					g.drawLine(x + 6, y + 10, x + dx, y + 10 + h);
				}
				if(lastThrust.rThrusterFired) {
					int  w = (int)(Math.random() * 7) + 10;
					int dy = (int)(Math.random() * 2) - 1;
					g.drawLine(x + 16, y - 15, x + 16 + w, y - 13 + dy);
					g.drawLine(x + 16, y - 11, x + 16 + w, y - 13 + dy);
				}
				if(lastThrust.lThrusterFired) {
					int  w = (int)(Math.random() * 7) + 10;
					int dy = (int)(Math.random() * 2) - 1;
					g.drawLine(x - 16, y - 15, x - 16 - w, y - 13 + dy);
					g.drawLine(x - 16, y - 11, x - 16 - w, y - 13 + dy);
				}
			}

			// victory flag
			if (success) {
				Rectangle flag = new Rectangle(x, y - 40, 10, 8);
				g.setColor(colors.background);
				g.fill(flag);
				g.setColor(colors.outline);
				g.draw(flag);
				g.drawLine(x, y - 40, x, y);
			}
	
			// base
			fill(g, new Rectangle(x - 15, y - 3, 30, 6));

			// left leg
			fill(g, new Polygon(
				new int[] { x - 13, x - 9, x - 17 },
				new int[] { y +  3, y + 3, y + 13 },
				3
			));
			g.drawLine(x - 19, y + 13, x - 15, y + 13);

			// right leg
			fill(g, new Polygon(
				new int[] { x + 13, x + 9, x + 17 },
				new int[] { y +  3, y + 3, y + 13 },
				3
			));
			g.drawLine(x + 19, y + 13, x + 15, y + 13);

			// crew module
			fill(g, new Polygon(
				new int[] { x - 7, x - 13, x - 13, x -  7, x +  7, x + 13, x + 13, x + 7 },
				new int[] { y - 3, y - 10, y - 17, y - 24, y - 24, y - 17, y - 10, y - 3 },
				8
			));

			// main thruster
			fill(g, new Polygon(
				new int[] { x - 5, x -  8, x +  8, x + 5 },
				new int[] { y + 3, y + 10, y + 10, y + 3 },
				4
			));

			// right thruster
			fill(g, new Polygon(
				new int[] { x + 13, x + 16, x + 16, x + 13 },
				new int[] { y - 14, y - 17, y -  9, y - 12 },
				4
			));

			// left thruster
			fill(g, new Polygon(
				new int[] { x - 13, x - 16, x - 16, x - 13 },
				new int[] { y - 14, y - 17, y -  9, y - 12 },
				4
			));
		}

		void fill(Graphics2D g, Shape shape) {
			g.setColor(colors.background);
			g.fill(shape);
			g.setColor(new Color(100, 100, 100));
			g.draw(shape);
		}
	}
}
