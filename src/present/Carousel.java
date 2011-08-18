package present;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

import vitro.*;
import vitro.util.*;
import vitro.graph.*;
import vitro.grid.*;
import vitro.plane.*;
import vitro.plane.Position;
import demos.reversi.*;
import demos.vacuum.*;
import demos.sweeper.*;
import demos.lights.*;
import demos.tictac.*;
import demos.lunar.*;
import demos.robots.*;
import assign.search.*;

public class Carousel extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private static final int SCREEN_WIDTH  = 1024;
	private static final int SCREEN_HEIGHT = 768;
	private static final double ZOOM_TIME = 8;
	private static final double PAN_TIME = 10;
	private final Object paintMonitor = new Object();

	public static void main(String[] args) {
		JFrame window = new JFrame();
		Carousel app = new Carousel();
		window.add(app);
		window.addKeyListener(app);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setUndecorated(true);

		// hide the cursor
		//Toolkit toolbox = Toolkit.getDefaultToolkit();
		//Image cursorImage = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
		//window.setCursor(toolbox.createCustomCursor(cursorImage,new Point(0,0),""));

		// pop into fullscreen mode
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice      gd = ge.getDefaultScreenDevice();
		gd.setFullScreenWindow(window);

		// try to switch to the appropriate display resolution
		if (gd.isDisplayChangeSupported()) {
			for(DisplayMode m : gd.getDisplayModes()) {
				if (m.getWidth()  != SCREEN_WIDTH)  { continue; }
				if (m.getHeight() != SCREEN_HEIGHT) { continue; }
				gd.setDisplayMode(m);
				break;
			}
		}

		window.pack();

		while(true) {
			app.repaint();
			app.tick();
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public Carousel() {
		setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
	}

	private int getViewX(int index) {
		index %= grid[0].length * grid.length;
		return index % grid[0].length;
	}
	private int getViewY(int index) {
		index %= grid[0].length * grid.length;
		return index / grid[0].length;
	}
	private View getView(int index) {
		return grid[getViewY(index)][getViewX(index)].view;
	}
	private void resetView(int index) {
		grid[getViewY(index)][getViewX(index)].reset();
	}
	private boolean waitForTimer(int max) {
		timer++;
		if (timer >= max) {
			timer = 0;
			return false;
		}
		return true;
	}

	private State mode = State.Grid;
	private int index = 0;
	private double timer = 0;
	private Image buffer = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	private Tweener tl;
	private Tweener br;
	private Tweener tl2;
	private Tweener br2;

	public void tick() {
		if (mode == State.Grid) {
			if (waitForTimer(150)) { return; }
			advance(State.ZoomIn);
			zoomTransition(false);
		}
		else if (mode == State.ZoomIn) {
			tl.tick(.1);
			br.tick(.1);
			if (tl.done() && br.done()) {
				if (waitForTimer(20)) { return; }
				mode = State.Slide;
			}
		}
		else if (mode == State.Pan) {
			tl.tick(.1);
			br.tick(.1);
			tl2.tick(.1);
			br2.tick(.1);
			if (tl.done() && br.done() && tl2.done() && br2.done()) {
				if (waitForTimer(20)) { return; }
				mode = State.Slide;
			}
		}
		else if (mode == State.Slide) {
			View v = getView(index);
			v.tick(.01);
			if (v.controller().hasNext()) { return; }
			if (waitForTimer(150)) { return; }

			synchronized(paintMonitor) {
				if (Math.random() < .2) { zoomTransition(true); }
				else                    { slideTransition(); }
			}
		}
		else if (mode == State.ZoomOut) {
			tl.tick(.1);
			br.tick(.1);
			if (tl.done() && br.done()) {
				if (waitForTimer(20)) { return; }
				mode = State.Grid;
			}
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		synchronized(paintMonitor) {
			if (mode == State.Grid) {
				drawGrid(g);
			}
			else if (mode == State.ZoomIn || mode == State.ZoomOut) {
				drawGrid(g);
				drawAt(getView(index), tl, br, g);
			}
			else if (mode == State.Pan) {
				drawAt(getView(index-1), tl,  br,  g);
				drawAt(getView(index),   tl2, br2, g);
			}
			else if (mode == State.Slide) {
				drawToBuffer(getView(index), (Graphics2D)g);
			}
		}
	}

	private void slideTransition() {
		advance(State.Pan);
		int w = SCREEN_WIDTH;
		int h = SCREEN_HEIGHT;
		boolean positive = Math.random() > .5;
		boolean vertical = Math.random() > .5;
		int s = vertical ? h : w;
		int a = positive ?     s :    -s;
		int b = positive ? 2 * s :     0;
		int c = positive ?     0 : 2 * s;
		if (vertical) {
			tl  = new Tweener( 0, 0, 0, a, PAN_TIME);
			br  = new Tweener( w, h, w, b, PAN_TIME);
			tl2 = new Tweener( 0,-a, 0, 0, PAN_TIME);
			br2 = new Tweener( w, c, w, h, PAN_TIME);
		}
		else {
			tl  = new Tweener( 0, 0, a, 0, PAN_TIME);
			br  = new Tweener( w, h, b, h, PAN_TIME);
			tl2 = new Tweener(-a, 0, 0, 0, PAN_TIME);
			br2 = new Tweener( c, h, w, h, PAN_TIME);
		}
	}

	private void zoomTransition(boolean out) {
		mode = out ? State.ZoomOut : State.ZoomIn;
		int x = getViewX(index);
		int y = getViewY(index);
		int w = SCREEN_WIDTH  / grid[0].length;
		int h = SCREEN_HEIGHT / grid.length;
		if (out) {
			tl = new Tweener(            0,             0,  x    * w,  y    * h, ZOOM_TIME);
			br = new Tweener( SCREEN_WIDTH, SCREEN_HEIGHT, (x+1) * w, (y+1) * h, ZOOM_TIME);
		}
		else {
			tl = new Tweener( x    * w,  y    * h,            0,             0, ZOOM_TIME);
			br = new Tweener((x+1) * w, (y+1) * h, SCREEN_WIDTH, SCREEN_HEIGHT, ZOOM_TIME);
		}
	}

	private void advance(State nextState) {
		synchronized(paintMonitor) {
			index++;
			resetView(index);
			mode = nextState;
		}
	}

	private void drawAt(View v, Tweener a, Tweener b, Graphics g) {
		drawToBuffer(v);
		g.drawImage(
			buffer,
			a.x(),
			a.y(),
			b.x(),
			b.y(),
			0,
			0,
			SCREEN_WIDTH,
			SCREEN_HEIGHT,
			this
		);
	}

	private void drawToBuffer(View v) {
		drawToBuffer(v, (Graphics2D)buffer.getGraphics());
	}

	private void drawToBuffer(View v, Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		g.translate(
			(SCREEN_WIDTH  - v.width())  / 2,
			(SCREEN_HEIGHT - v.height()) / 2
		);
		v.draw(g);
	}

	private void drawGrid(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for(int y = 0; y < grid.length; y++) {
			for(int x = 0; x < grid[0].length; x++) {
				drawToBuffer(grid[y][x].view);
				g.drawImage(
					buffer,
					x * ( SCREEN_WIDTH  / grid[0].length ),
					y * ( SCREEN_HEIGHT / grid.length ),
					(x + 1) * ( SCREEN_WIDTH  / grid[0].length ),
					(y + 1) * ( SCREEN_HEIGHT / grid.length ),
					0,
					0,
					SCREEN_WIDTH,
					SCREEN_HEIGHT,
					this
				);
			}
		}
	}

	public void keyReleased(KeyEvent k) {
		if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { System.exit(0); }
	}
	public void keyPressed(KeyEvent k) {}
	public void keyTyped(KeyEvent k) {}

	private void createDense(GraphView view, Node... nodes) {
		for(int x = 0; x < nodes.length; x++) {
			for(int y = 0; y < nodes.length; y++) {
				if(x != y) { view.createEdge(nodes[x], nodes[y]); }
			}
		}
	}

	Slide[][] grid = {
		{
			new Slide() { public void reset() {
				Reversi model                   = new Reversi(8, 8);
				SequentialController controller = new SequentialController(model);
				ReversiView view                = new ReversiView(model, controller, SCREEN_WIDTH, SCREEN_HEIGHT, new ColorScheme());
				Reversi.Player black = model.createPlayer(Reversi.BLACK);
				Reversi.Player white = model.createPlayer(Reversi.WHITE);
				model.actors.add(black);
				model.actors.add(white);
				controller.bind(black, new ReversiBrain());
				controller.bind(white, new ReversiBrain());
				this.view = view;
			}},
			new Slide() { public void reset() {
				int[][] maze = Math.random() < .3 ?
				new int[][] {
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 0 },
					{ 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
				} : Math.random() > .5 ?
				new int[][] {
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 3, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
				} :
				new int[][] {
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
					{ 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0 },
					{ 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0 },
					{ 0, 3, 0, 0, 0, 0, 1, 1, 1, 0, 3, 0 },
					{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
				};

				Robots model = new Robots(maze);
				model.locations.put(model.createBLU()  , new Location(model, 1, 1));
				model.locations.put(model.createBlock(), new Location(model, 2, 2));
				Controller controller = new SequentialController(model);
				RobotsView view       = new RobotsView(model, controller);
				controller.bind(Robots.BLU.class, new SokobanAgentBLU());
				this.view = view;
			}},
			new Slide() { public void reset() {
				LightsOut model                 = new LightsOut(13, 13);
				SequentialController controller = new SequentialController(model);
				LightsOutView view              = new LightsOutView(model, controller, SCREEN_WIDTH, SCREEN_HEIGHT);
				controller.bind(LightsOut.Player.class, new LightsOutBrain());
				model.shuffle();
				this.view = view;
			}},
		},
		{
			new Slide() { public void reset() {
				Sweeper model         = new Sweeper(58, 40, 300);
				Controller controller = new SimultaneousController(model);
				SweeperView view      = new SweeperView(model, controller);
				controller.bind(model.player, new SweeperAgent());
				model.clearSafeArea();
				this.view = view;
			}},
			new Slide() { public void reset() {
				VacWorld model                  = new VacWorld();
				SequentialController controller = new SequentialController(model);
				GraphView view                  = new GraphView(model, controller, SCREEN_WIDTH, SCREEN_HEIGHT);
				controller.bind(VacWorld.Scrubby.class, new VacBrain());

				Node entrance    = view.createNode(.05, .58, "Entrance"    );
				Node hallway0    = view.createNode(.15, .58, "Hallway"     );
				Node hallway1    = view.createNode(.40, .58, "Hallway"     );
				Node hallway2    = view.createNode(.65, .58, "Hallway"     );
				Node hallway3    = view.createNode(.90, .58, "Hallway"     );
				Node living0     = view.createNode(.15, .74, "Living Room" );
				Node living1     = view.createNode(.40, .74, "Living Room" );
				Node living2     = view.createNode(.65, .74, "Living Room" );
				Node living3     = view.createNode(.15, .90, "Living Room" );
				Node living4     = view.createNode(.40, .90, "Living Room" );
				Node living5     = view.createNode(.65, .90, "Living Room" );
				Node bathroom0   = view.createNode(.90, .90, "Bathroom"    );
				Node bathroom1   = view.createNode(.90, .74, "Bathroom"    );
				Node kitchen0    = view.createNode(.15, .42, "Kitchen"     );
				Node kitchen1    = view.createNode(.40, .42, "Kitchen"     );
				Node kitchen2    = view.createNode(.15, .26, "Kitchen"     );
				Node kitchen3    = view.createNode(.40, .26, "Kitchen"     );
				Node bedroom0    = view.createNode(.65, .42, "Bedroom"     );
				Node bedroom1    = view.createNode(.90, .42, "Bedroom"     );
				Node bedroom2    = view.createNode(.65, .26, "Bedroom"     );
				Node bedroom3    = view.createNode(.90, .26, "Bedroom"     );
				Node bedroom4    = view.createNode(.65, .10, "Bedroom"     );
				Node bedroom5    = view.createNode(.90, .10, "Bedroom"     );
				Node closet0     = view.createNode(.15, .10, "Closet"      );
				Node closet1     = view.createNode(.40, .10, "Closet"      );

				createDense(view, closet0, closet1);
				createDense(view, kitchen0, kitchen1, kitchen2, kitchen3);
				createDense(view, bedroom0, bedroom1, bedroom2, bedroom3, bedroom4, bedroom5);
				createDense(view, living0, living1, living2, living3, living4, living5);
				createDense(view, bathroom0, bathroom1);
				createDense(view, entrance, hallway0);
				createDense(view, hallway0, hallway1);
				createDense(view, hallway1, hallway2);
				createDense(view, hallway2, hallway3);
				createDense(view, bedroom4 , closet1 );
				createDense(view, bathroom1, hallway3);
				createDense(view, bedroom1 , hallway3);
				createDense(view, living1  , hallway1);
				createDense(view, kitchen1 , hallway1);
				
				for(int x = 8; x > 0; x--) {
					model.nodes.get(x).actors.add(model.createDirt());
				}
				
				for(int x = model.nodes.size() - 1; x > 0; x--) {
					int r = (int)(Math.random() * (x + 1));
					List<Actor> xDirt = Groups.ofType(VacWorld.Dirt.class, model.nodes.get(x).actors);
					List<Actor> rDirt = Groups.ofType(VacWorld.Dirt.class, model.nodes.get(r).actors);
					model.nodes.get(x).actors.removeAll(xDirt);
					model.nodes.get(r).actors.removeAll(rDirt);
					model.nodes.get(x).actors.addAll(rDirt);
					model.nodes.get(r).actors.addAll(xDirt);
				}
				
				closet0.actors.add(model.createScrubby());
				this.view = view;
			}},
			new Slide() { public void reset() {
				/*
				Lunar model = new Lunar(-1.0, new Position(0.0, 10.0));
				Lander lander = new Lander(model);
				model.positions.put(lander, new Position(000.0, 400.0));
				lander.velocity = new Vector2(0.0, 0.0);
				lander = new Lander(model);
				model.positions.put(lander, new Position(-100.0, 400.0));
				lander.velocity = new Vector2(5.0, 0.0);
				Controller controller = new SequentialController(model);
				LunarView view        = new LunarView(model, controller, SCREEN_WIDTH, SCREEN_HEIGHT);
				*/
				TicTac     model      = new TicTac(3);
				Controller controller = new SequentialController(model);
				TicTacView view       = new TicTacView(model, controller);
				controller.bind(model.player0, new RandomAgent());
				controller.bind(model.player1, new RandomAgent());
				this.view = view;
			}},
		}
	};
}

enum State { Grid, ZoomIn, Slide, ZoomOut, Pan };

abstract class Slide {
	public View view;
	public Slide() { reset(); }
	public abstract void reset();
}

class LinearTweener extends Tweener {
	
	public LinearTweener(int x1, int y1, int x2, int y2, double length) {
		super(x1, y1, x2, y2, length);
	}

	protected double tween(double a, double b, double t) {
		return (a * (1-t)) + (b * t);
	}
}