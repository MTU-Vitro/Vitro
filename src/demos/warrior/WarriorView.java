package demos.warrior;

import demos.*;
import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.util.List;
import static vitro.util.Groups.*;

public class WarriorView implements View {

	private final Controller controller;
	private final ColorScheme colors = new ColorScheme();

	private final BufferedImage buffer;
	private final int width;
	private final int height;

	private final Image tiles;
	private final Image sprites;

	public WarriorView(Controller controller) {
		this.controller = controller;
		this.buffer = new BufferedImage(
			model().width  * 16,
			model().height * 16,
			BufferedImage.TYPE_INT_ARGB
		);
		this.width  = 64 * model().width;
		this.height = 64 * model().height;

		try {
			ClassLoader loader = WarriorView.class.getClassLoader();
			this.tiles   = ImageIO.read(loader.getResource("demos/warrior/warriorTiles.png"));
			this.sprites = ImageIO.read(loader.getResource("demos/warrior/warriorSprites.png"));
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

	protected Warrior model() { return (Warrior)controller.model(); }

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	public void flush() {
		
	}

	private double sofar = -1;
	public void tick(double time) {
		sofar += time;
		if (sofar > .5) {
			controller.next();
			sofar = 0;
		}
	}

	public void draw(Graphics2D g) {
		synchronized(model()) {
			Graphics gd = buffer.getGraphics();

			// draw grid tiles
			for(int y = 0; y < model().height; y++) {
				for(int x = 0; x < model().width; x++) {
					int tile = 7;
					switch(model().world[y][x]) {
						case Warrior.FLOOR: tile = 0; break;
						case Warrior.SOLID: tile = 1; break;
						case Warrior.STAIR: tile = 2; break;
						case Warrior.START:
						case Warrior.SLIME:
						case Warrior.GEM:   tile = 5; break;
					}
					if (tile == 1 && y < model().height-1 && model().world[y+1][x] != Warrior.SOLID) {
						tile = 3;
					}
					int tx = (tile % (tiles.getWidth(null) / 16));
					int ty = (tile / (tiles.getWidth(null) / 16));
					gd.drawImage(
						tiles,
						 x * 16,  y * 16, ( x+1) * 16, ( y+1) * 16,
						tx * 16, ty * 16, (tx+1) * 16, (ty+1) * 16,
						null
					);
				}
			}
			
			// draw sprites for actors
			for(Actor a : model().actors) {
				int tile = -1;
				if (a instanceof Warrior.Hero)  { tile = 0; }
				if (a instanceof Warrior.Gem)   { tile = 2; }
				if (a instanceof Warrior.Slime) { tile = 3; }
				if (tile < 0) { continue; }
				int tx = (tile % (sprites.getWidth(null) / 16));
				int ty = (tile / (sprites.getWidth(null) / 16));
				Location l = model().locations.get(a);
				int x = l.x;
				int y = l.y;
				gd.drawImage(
					sprites,
						 x * 16,  y * 16 - 4, ( x+1) * 16, ( y+1) * 16 - 4,
						tx * 16, ty * 16,     (tx+1) * 16, (ty+1) * 16,
					null
				);
			}

			// draw grid annotations (ensuring a consistent ordering to annotation drawing)
			for(Annotation a : new TreeSet<Annotation>(Groups.ofType(GridAnnotation.class, controller.annotations().keySet()))) {
				for(Map.Entry<Point, Color> tile : ((GridAnnotation)a).coloring.entrySet()) {
					gd.setColor(tile.getValue());
					gd.fillRect(
						(tile.getKey().x * 16),
						(tile.getKey().y * 16),
						16,
						16
					);
				}
			}

			// draw overhangs
			gd.setColor(Color.BLACK);
			for(int y = 1; y < model().height; y++) {
				for(int x = 0; x < model().width; x++) {
					if (model().world[y  ][x] != Warrior.SOLID) { continue; }
					if (model().world[y-1][x] == Warrior.SOLID) { continue; }
					gd.fillRect(x * 16, y * 16 - 8, 16, 8);
				}
			}
		}

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);

		g.drawImage(
			buffer,
			0, 0, width, height,
			0, 0, buffer.getWidth(null), buffer.getHeight(null),
			null
		);
	}
}
