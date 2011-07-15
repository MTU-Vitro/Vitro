package demos.robots;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.util.List;
import static vitro.util.Groups.*;

public class RobotsView implements View {

	private final Controller controller;
	private final ColorScheme colors = new ColorScheme();
	private final int width  = 720;
	private final int height = 720;
	
	private final Robots model;
	private final Image buffer;
	private final Image tiles;
	private final Image BLU;
	private final Image RNG;
	private final Image crate;
	private final Map<Actor, Sprite> sprites = new HashMap<Actor, Sprite>();
	private final List<Sprite> renderSprites = new ArrayList<Sprite>();

	private int actionIndex = 0;
 
	private static final Map<Integer, Integer> bigTiles = new HashMap<Integer, Integer>();
	{
		bigTiles.put(Robots.SOLID, -1);
		bigTiles.put(Robots.LIGHT,  0);
		bigTiles.put(Robots.DARK,   0);
		bigTiles.put(Robots.TARGET, 1);
	}

	public RobotsView(Robots model, Controller controller) {
		this.controller = controller;
		this.model = model;
		this.buffer = new BufferedImage(
			model.width  * 16,
			model.height * 16,
			BufferedImage.TYPE_INT_RGB
		);
		try {
			ClassLoader loader = RobotsView.class.getClassLoader();
			this.tiles = ImageIO.read(loader.getResource("demos/robots/sokotiles.png"));
			this.BLU   = ImageIO.read(loader.getResource("demos/robots/BLU.png"));
			this.RNG   = ImageIO.read(loader.getResource("demos/robots/RNG.png"));
			this.crate = ImageIO.read(loader.getResource("demos/robots/Crate.png"));
		}
		catch(IOException e) {
			throw new Error("Unable to load image resources.");
		}
		flush();

		colors.outline    = Color.WHITE;
		colors.secondary  = Color.GRAY;
		colors.background = Color.BLACK;
		colors.inactive   = Color.GRAY.darker();
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	private double sofar = -1;
	public void tick(double time) {
		sofar += time;
		if (sofar > .5) {
			if (actionIndex >= controller.previousActions().size()) {
				flush();
				controller.next();
				actionIndex = 0;
			}
			else {
				if (showAction(controller.previousActions().get(actionIndex))) {
					actionIndex++;
				}
			}
			sofar = 0;
		}
	}

	private boolean showAction(Action a) {
		if (a instanceof Robots.PushAction) {
			Robots.PushAction p = (Robots.PushAction)a;
			return moveTo(p.pusher, p.pushedFrom) &
			       moveTo(p.pushed, p.pushedTo);
		}
		/*
		// this is a destroyAction, so for now
		// we can go without custom behavior.
		else if (a instanceof Robots.FloatAction) {}
		*/
		else if (a instanceof MoveAction) {
			MoveAction m = (MoveAction)a;
			return moveTo(m.actor, m.end);
		}
		else if (a instanceof CompositeAction) {
			CompositeAction c = (CompositeAction)a;
			MoveAction m1 = (MoveAction)c.actions.get(0);
			MoveAction m2 = (MoveAction)c.actions.get(1);
			return moveTo(m1.actor, m1.end) &
			       moveTo(m2.actor, m2.end);
		}
		else if (a instanceof DestroyAction) {
			DestroyAction d = (DestroyAction)a;
			for(Actor actor : d.actors.keySet()) {
				Sprite sprite = sprites.get(actor);
				if (sprite == null) { continue; }
				if (actor instanceof Robots.RNG) {
					sprite.anim = new int[] { 7, 8 };
				}
				else {
					sprite.anim = new int[] { 0 };
				}
				sprite.frame = 0;
			}
			return true;
		}
		else {
			System.out.println("Unhandled action: "+a);
			return true;
		}
	}

	private boolean moveTo(Actor actor, Location location) {
		final Sprite sprite = sprites.get(actor);
		final int sx = screenX(location);
		final int sy = screenY(location);
		if (sprite.x < sx) { sprite.x++; }
		if (sprite.x > sx) { sprite.x--; }
		if (sprite.y < sy) { sprite.y++; }
		if (sprite.y > sy) { sprite.y--; }

		if (actor instanceof Robots.BLU) {
			// walking animations
			if      (sprite.x < sx) { sprite.anim = new int[] { -6, -7, -6, -5 }; } // walk right
			else if (sprite.x > sx) { sprite.anim = new int[] {  6,  7,  6,  5 }; } // walk left
			else if (sprite.y < sy) { sprite.anim = new int[] {  1,  2, -1, -2 }; } // walk down
			else if (sprite.y > sy) { sprite.anim = new int[] {  3,  4, -3, -4 }; } // walk up
			else                    { sprite.anim = new int[] { 1 }; sprite.frame = 0; }
		}

		if (actor instanceof Robots.RNG) {
			if (sprite.x == sx && sprite.y == sy) {
				// toggle active/inactive animations
				if (model.dark(location)) {
					sprite.anim = new int[] { 6 };
					sprite.frame = 0;
					sprite.z = 0;
				}
				else {
					sprite.anim = new int[] { 1, 2, -1, -2 };
					sprite.z = 1;
				}
			}
		}

		return sprite.x == sx && sprite.y == sy;
	}

	private int screenX(Location location) { return location.x * 16; }
	private int screenY(Location location) { return location.y * 16 - 8; }

	public void flush() {
		sprites.clear();
		synchronized(model) {
			for(Actor actor : model.actors) {
				Location location = model.locations.get(actor);
				if (location == null) { continue; }
				if (actor instanceof Robots.BLU) {
					sprites.put(actor, new Sprite(
						BLU,
						new int[] { 1, 8 },
						screenX(location),
						screenY(location)
					));
				}
				else if (actor instanceof Robots.RNG) {
					Sprite r = new RNGSprite(
						RNG,
						new int[]{ 1, 2, -1, -2 },
						screenX(location),
						screenY(location)
					);
					if (model.dark(location)) {
						r.anim = new int[] { 6 };
						r.z = 0;
					}
					sprites.put(actor, r);
				}
				else if (actor instanceof Robots.Block) {
					sprites.put(actor, new Sprite(
						crate,
						new int[] { 1 },
						screenX(location),
						screenY(location)
					));
				}
			}
			for(int y = 0; y < model.height; y++) {
				for(int x = 0; x < model.width; x++) {
					if (model.dark(new Location(model, x, y))) {
						boolean top = (y == 0 || !model.dark(new Location(model, x, y-1)));
						sprites.put(
							new Actor(),
							new Shadow(x * 16, y * 16, top)
						);
					}
				}
			}
		}
	}

	public void draw(Graphics g) {
		if (sofar < 0) { flush(); }
		synchronized(model) {
			if (model.done() && actionIndex < controller.previousActions().size()) {
				if (showAction(controller.previousActions().get(actionIndex))) {
					actionIndex++;
				}
			}
		}

		Graphics2D bg = (Graphics2D)buffer.getGraphics();
		bg.setColor(colors.background);
		bg.fillRect(0, 0, width, height);

		// draw background:
		for(int y = 0; y < model.height; y++) {
			for(int x = 0; x < model.height; x++) {
				drawCell(bg, x, y);
			}
		}
		
		// draw sprites:
		renderSprites.clear();
		renderSprites.addAll(sprites.values());
		Collections.sort(renderSprites);
		for(Sprite sprite : renderSprites) {
			sprite.draw(bg);
		}

		g.drawImage(
			buffer,
			0, 0, width,                 height,
			0, 0, buffer.getWidth(null), buffer.getHeight(null),
			null
		);
	}

	protected void drawCell(Graphics2D g, int x, int y) {
		if (!bigTiles.containsKey(model.tiles[y][x])) {
			g.setColor(Color.RED.darker().darker());
			g.fillRect(
				(x * 16),
				(y * 16),
				16,
				16
			);
		}
		else {
			int tile = bigTiles.get(model.tiles[y][x]);
			if (tile == -1) { return; }
			int sw = (tiles.getWidth(null) / 16);
			int tx = (tile % sw) * 16;
			int ty = (tile / sw) * 16;
			g.drawImage(
				tiles,
				x * 16, y * 16, (x+1)*16, (y+1)*16,
				tx, ty, tx + 16, ty + 16,
				null
			);
		}
	}

	private static class Sprite implements Comparable<Sprite> {
		private final Image tiles;
		public int[] anim;
		public int frame = 0;
		public int delay = 20;
		public int draws = 0;
		public int x;
		public int y;
		public int z;

		public Sprite(Image tiles, int[] anim, int x, int y) {
			this.tiles =  tiles;
			this.anim = anim;
			this.x = x;
			this.y = y;
			this.z = 0;
		}

		public void draw(Graphics g) {
			draws++;
			if (draws == delay) {
				frame = (frame + 1) % anim.length;
				draws = 0;
			}
			if (anim[frame] == 0) { return; }
			int sw = (tiles.getWidth(null) / 16);
			int tx = ((Math.abs(anim[frame]) - 1) % sw) * 16;
			int ty = ((Math.abs(anim[frame]) - 1) / sw) * 24;
			if (anim[frame] > 0) {
				g.drawImage(
					tiles,
					x,  y,  x + 16,  y + 24,
					tx, ty, tx + 16, ty + 24,
					null
				);
			}
			else {
				g.drawImage(
					tiles,
					x + 16,  y,  x,  y + 24,
					tx, ty, tx + 16, ty + 24,
					null
				);
			}
		}

		public int compareTo(Sprite o) {
			if (this.z > o.z) { return  1; }
			if (this.z < o.z) { return -1; }
			if (this.y > o.y) { return  1; }
			if (this.y < o.y) { return -1; }
			return 0;
		}
	}

	private static class RNGSprite extends Sprite {
		public RNGSprite(Image tiles, int[] anim, int x, int y) {
			super(tiles, anim, x, y);
			z = 1;
		}

		public void draw(Graphics g) {
			g.setColor(new Color(0, 0, 0, 100));
			g.fillRect( x + 4, y + 20, 8, 2 );
			g.fillRect( x + 6, y + 19, 4, 4 );
			super.draw(g);
		}
	}

	private static class Shadow extends Sprite {
		private boolean top;

		public Shadow(int x, int y, boolean top) {
			super(null, new int[] { 0 }, x, y);
			this.top = top;
		}

		public void draw(Graphics g) {
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(x, y, 16, 16);
			if (!top) { return; }
			g.setColor(new Color(0, 0, 0, 64));
			g.fillRect(x, y-8, 16, 8);
		}

		public int compareTo(Sprite o) {
			// shadows are always drawn after sprites in a cell:
			if (this.y == o.y && !(o instanceof Shadow)) { return 1; }
			return super.compareTo(o);
		}
	}
}