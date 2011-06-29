package demos;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import static vitro.util.Groups.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class LunarView implements View {

	private final int width;
	private final int height;

	private final LunarWorld model;
	private final Controller controller;
	private final ColorScheme colors;

	private final Image buffer;
	private final Image target;
	
	//private final Polygon mountains;
	double[] heightmap;
	LanderView landerView;

	public LunarView(LunarWorld model, Controller controller, int width, int height) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = new ColorScheme(Color.WHITE, new Color(75, 75, 75), Color.BLACK);

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		landerView = new LanderView(model.lander);
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	public void draw(Graphics g) {
		g.setColor(colors.background);
		g.fillRect(0, 0, width, height);

		g.setColor(colors.outline);
		g.drawLine(0, height - 20, width, height - 20);

		synchronized(model) {
			landerView.draw(g, (int)model.positions.get(model.lander).x, (int)model.positions.get(model.lander).y);
		}
	}

	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if(sofar > .75) {
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

		public boolean thrusterRight = true;
		public boolean thrusterLeft  = true;
		public boolean thrusterMain  = true;

		protected void draw(Graphics g, int x, int y) {
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

			g.setColor(new Color(100, 100, 100));
	
			// base
			g.drawRect(x - 15, y - 3, 30, 6);

			// left leg
			g.drawLine(x - 13, y +  3, x - 17, y + 13);
			g.drawLine(x -  9, y +  3, x - 17, y + 13);
			g.drawLine(x - 19, y + 13, x - 15, y + 13);

			// right leg
			g.drawLine(x + 13, y +  3, x + 17, y + 13);
			g.drawLine(x +  9, y +  3, x + 17, y + 13);
			g.drawLine(x + 19, y + 13, x + 15, y + 13);

			// crew module
			g.drawLine(x -  7, y -  3, x - 13, y - 10);
			g.drawLine(x +  7, y -  3, x + 13, y - 10);
			g.drawLine(x - 13, y - 10, x - 13, y - 17);
			g.drawLine(x + 13, y - 10, x + 13, y - 17);
			g.drawLine(x - 13, y - 17, x -  7, y - 24);
			g.drawLine(x + 13, y - 17, x +  7, y - 24);
			g.drawLine(x -  7, y - 24, x +  7, y - 24);

			// main thruster
			g.drawLine(x - 5, y +  3, x - 8, y + 10);
			g.drawLine(x + 5, y +  3, x + 8, y + 10);
			g.drawLine(x - 8, y + 10, x + 8, y + 10);

			// right thruster
			g.drawLine(x + 13, y - 14, x + 16, y - 17);
			g.drawLine(x + 13, y - 12, x + 16, y -  9);
			g.drawLine(x + 16, y - 17, x + 16, y -  9);

			// left thruster
			g.drawLine(x - 13, y - 14, x - 16, y - 17);
			g.drawLine(x - 13, y - 12, x - 16, y -  9);
			g.drawLine(x - 16, y - 17, x - 16, y -  9);
		}
	}
}
