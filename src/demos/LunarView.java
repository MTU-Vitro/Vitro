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

	public LunarView(LunarWorld model, Controller controller, int width, int height) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = new ColorScheme(Color.WHITE, new Color(75, 75, 75), Color.BLACK);
		
		landerView = new LanderView(model.lander);

		stars = new HashSet<Point>();
		for(int x = 0; x < 40; x++) {
			stars.add(new Point(
				(int)(Math.random() * width),
				(int)(Math.random() * height)
			));
		}

		int[] x = new int[50];
		int[] y = new int[50];
		int xp = -10;
		int yp = height / 2;
		for(int i = 1; i < 49; i++) {
			x[i] = xp;
			y[i] = yp;
			xp += (int)(Math.random() * 10) + (width / 40);
			yp += (int)(Math.random() * 40) - 20;
		}
		x[ 0] = -20;       y[ 0] = 2 * height;
		x[49] = 2 * width; y[49] = 2 * height;
		mountains = new Polygon(x, y, 50);
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	public void draw(Graphics g) {
		Drawing.configureVector(g);

		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);

		//g.setColor(colors.outline);
		//g.drawLine(0, height - 20, width, height - 20);

		g.setColor(colors.outline);
		for(Point star : stars) {
			g.drawLine(star.x, star.y, star.x, star.y);
		}

		g.setColor(colors.background);
		g.fillPolygon(mountains);
		g.setColor(new Color(50, 50, 50));
		g.drawPolygon(mountains);

		synchronized(model) {
			g.setColor(colors.outline);
			g.setFont(new Font("Monospaced", Font.BOLD, 20));
			g.drawString(String.format("VX:   % 2.3f", model.lander.velocity.x), 10, 20);
			g.drawString(String.format("VY:   % 2.3f", model.lander.velocity.y), 10, 40);
			g.drawString(String.format("FUEL: % d",    model.lander.fuel), 10, 60);

			for(Actor actor : model.actors) {
				if(actor instanceof Collidable) {
					AlignedBox bound = ((Collidable)actor).bound();
					g.drawRect(
						(int)bound.point0.x,
						(int)bound.point0.y,
						(int)(bound.point1.x - bound.point0.x),
						(int)(bound.point1.y - bound.point0.y)
					);
				}
			}

			Position position = model.positions.get(model.lander);
			landerView.draw(
				(Graphics2D)g,
				(int)position.x,
				(int)position.y
			);
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

		protected void draw(Graphics2D g, int x, int y) {
			g.setColor(Color.WHITE);
			LunarWorld.ThrusterAction lastThrust = null;
			
			if(controller.hasPrev()) {
				java.util.List<Action> actions = controller().previousActions();
				lastThrust = (LunarWorld.ThrusterAction)Groups.firstOfType(LunarWorld.ThrusterAction.class, actions);
			}
			
			if(lastThrust != null) {
				if(lastThrust.thrusterMain) {
					int  h = (int)(Math.random() * 10) + 20;
					int dx = (int)(Math.random() *  2)  - 1;
					g.drawLine(x - 6, y + 10, x + dx, y + 10 + h);
					g.drawLine(x + 6, y + 10, x + dx, y + 10 + h);
				}
				if(lastThrust.thrusterRight) {
					int  w = (int)(Math.random() * 7) + 10;
					int dy = (int)(Math.random() * 2) - 1;
					g.drawLine(x + 16, y - 15, x + 16 + w, y - 13 + dy);
					g.drawLine(x + 16, y - 11, x + 16 + w, y - 13 + dy);
				}
				if(lastThrust.thrusterLeft) {
					int  w = (int)(Math.random() * 7) + 10;
					int dy = (int)(Math.random() * 2) - 1;
					g.drawLine(x - 16, y - 15, x - 16 - w, y - 13 + dy);
					g.drawLine(x - 16, y - 11, x - 16 - w, y - 13 + dy);
				}
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
