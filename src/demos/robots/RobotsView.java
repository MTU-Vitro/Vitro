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
	private final Image crate;
	private final Map<Actor, Sprite> sprites = new HashMap<Actor, Sprite>();
	private final List<Sprite> renderSprites = new ArrayList<Sprite>();

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
		if (sofar < 0) { flush(); }
		sofar += time;
		if (sofar > 10) {
			controller.next();
			flush();
			sofar = 0;
		}
	}

	public void flush() {
		sprites.clear();
		synchronized(model) {
			for(Actor actor : model.actors) {
				Location location = model.locations.get(actor);
				if (location == null) { continue; }
				if (actor instanceof Robots.BLU) {
					sprites.put(actor, new Sprite(
						BLU,
						0,
						location.x * 16,
						location.y * 16 - 8
					));
				}
				else if (actor instanceof Robots.Block) {
					sprites.put(actor, new Sprite(
						crate,
						0,
						location.x * 16,
						location.y * 16 - 8
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
		Graphics2D bg = (Graphics2D)buffer.getGraphics();
		bg.setColor(colors.background);
		bg.fillRect(0, 0, width, height);

		// draw background:
		for(int y = 0; y < model.height; y++) {
			for(int x = 0; x < model.height; x++) {
				drawCell(bg, x, y);
			}
		}
		
		// draw sprites: (with y-sorting)
		renderSprites.clear();
		renderSprites.addAll(sprites.values());
		Collections.sort(renderSprites);
		for(Sprite sprite : renderSprites) {
			sprite.draw(bg);
		}

		g.drawImage(
			buffer,
			0, 0,           width,                 height,
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
		public int frame;
		public int state = 0;
		public int x;
		public int y;

		public Sprite(Image tiles, int frame, int x, int y) {
			this.tiles =  tiles;
			this.frame = frame;
			this.x = x;
			this.y = y;
		}

		public void draw(Graphics g) {
			if (frame == -1) { return; }
			int sw = (tiles.getWidth(null) / 16);
			int tx = (frame % sw) * 16;
			int ty = (frame / sw) * 24;
			g.drawImage(
				tiles,
				 x,  y,  x + 16,  y + 24,
				tx, ty, tx + 16, ty + 24,
				null
			);
		}

		public int compareTo(Sprite o) {
			if (this.y > o.y) { return  1; }
			if (this.y < o.y) { return -1; }
			return 0;
		}
	}

	private class Shadow extends Sprite {
		private boolean top;

		public Shadow(int x, int y, boolean top) {
			super(null, 0, x, y);
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