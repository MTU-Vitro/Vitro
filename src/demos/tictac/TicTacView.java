package demos.tictac;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.util.List;
import static vitro.util.Groups.*;

public class TicTacView implements View {

	private final Controller controller;
	private final ColorScheme colors = new ColorScheme();
	private final int width  = 500;
	private final int height = 595 + 123;
	
	private final TicTac model;
	private final Image board;
	private final Image crosses;
	private final Image messages;

	public TicTacView(TicTac model, Controller controller) {
		this.controller = controller;
		this.model = model;
		try {
			ClassLoader loader = TicTacView.class.getClassLoader();
			this.board    = ImageIO.read(loader.getResource("demos/tictac/board.png"));
			this.crosses  = ImageIO.read(loader.getResource("demos/tictac/crosses.png"));
			this.messages = ImageIO.read(loader.getResource("demos/tictac/messages.png"));
		}
		catch(IOException e) {
			throw new Error("Unable to load image resources.");
		}
	}

	public Controller  controller()  { return controller; }
	public ColorScheme colorScheme() { return colors;     }
	public int         width()       { return width;      }
	public int         height()      { return height;     }

	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if (sofar > 5) {
			controller.next();
			sofar = 0;
		}
	}

	public void flush() {
		// nuffing
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.drawImage(board, 0, 123, null);

		synchronized(model) {
			if (model.done()) {
				if (model.hasWon(model.CROSSES)) { drawMessage(1, g); }
				if (model.hasWon(model.CIRCLES)) { drawMessage(3, g); }
			}
			else {
				drawMessage(model.team() * 2, g);
			}

			for(int x = 0; x < 3; x++) {
				for(int y = 0; y < 3; y++) {
					Actor a = model.actorAt(new Location(model, x, y));
					if (a instanceof Factional) {
						drawCross(x, y, ((Factional)a).team() == model.CROSSES, g);
					}
				}
			}
		}
	}

	private void drawMessage(int index, Graphics g) {
		g.drawImage(
			messages,
			0,
			0,
			495,
			123,
			0,
			123 * index,
			495,
			123 * (index + 1),
			null
		);
	}

	private void drawCross(int x, int y, boolean blue, Graphics g) {
		int tile = (x + (y * 2)) % 6;
		int px = x * 166 + 20;
		int py = y * 210 + 20 + 123;

		g.drawImage(
			crosses,
			px,
			py,
			px + 108,
			py + 108,
			tile * 108,
			(blue ? 0 : 1) * 108,
			(tile + 1) * 108,
			(blue ? 1 : 2) * 108,
			null
		);
	}
}