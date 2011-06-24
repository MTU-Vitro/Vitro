package demos;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.image.*;

public class ReversiView implements View {

	private final int width;
	private final int height;
	private final int cellSize;
	private final int cellMargin;
	private final int horizontalMargin;
	private final int verticalMargin;

	private final Reversi model;
	private final Controller controller;
	private final ColorScheme colors;

	private final Image buffer;
	private final Image target;

	public ReversiView(Reversi model, Controller controller, int width, int height, ColorScheme colors) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = colors;

		cellSize = (int)Math.min(
			width  * .8 / model.width,
			height * .8 / model.height
		);
		horizontalMargin = (width  - (model.width  * cellSize)) / 2;
		verticalMargin   = (height - (model.height * cellSize)) / 2;
		cellMargin       = cellSize / 10;

		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	public Controller controller()   { return controller; }
	public ColorScheme colorScheme() { return colors; }

	public Image getBuffer() {
		synchronized(target) {
			buffer.getGraphics().drawImage(target, 0, 0, null);
		}
		return buffer;
	}

	public void draw() {
		Graphics g = target.getGraphics();
		synchronized(target) {
			g.setColor(colors.background);
			g.fillRect(0, 0, width, height);
			Drawing.configureVector(g);
			g.setFont(new Font("Monospaced", Font.BOLD, 36));

			for(int y = 0; y < model.height; y++) {
				for(int x = 0; x < model.height; x++) {
					g.setColor(colors.outline);
					g.drawRect(
						horizontalMargin + (x * cellSize),
						verticalMargin   + (y * cellSize),
						cellSize,
						cellSize
					);
				}
			}
			g.setColor(colors.secondary);
			g.fillRect(
				horizontalMargin,
				verticalMargin + (model.height * cellSize) + 1,
				(model.width * cellSize) + 1,
				(cellSize / 10)
			);

			synchronized(model) {
				for(Actor a : model.actors) {
					if (a instanceof Reversi.Player) {
						Reversi.Player p = (Reversi.Player)a;
						if (p.team() != model.team()) { continue; }
						for(Action act : p.actions()) {
							if (!(act instanceof Reversi.Move)) { continue; }
							Reversi.Move move = (Reversi.Move)act;
							Location moveLocation = move.location;
							g.setColor(colors.unique(new Integer(p.team())));
							g.drawOval(
								horizontalMargin + cellMargin + (moveLocation.x * cellSize) + 1,
								verticalMargin   + cellMargin + (moveLocation.y * cellSize) + 1,
								cellSize - (cellMargin * 2) - 1,
								cellSize - (cellMargin * 2)
							);
							g.setColor(colors.unique(new Integer(p.team())).darker());
							Drawing.drawStringCentered(
								g,
								""+move.captured.size(),
								horizontalMargin + cellSize/2 + (moveLocation.x * cellSize),
								verticalMargin   + cellSize/2 + (moveLocation.y * cellSize)
							);
						}
						continue;
					}
					Location location = model.locations.get(a);
					if (location == null) { continue; }
					if (a instanceof Factional) {
						g.setColor(colors.unique(new Integer(((Factional)a).team())));
					}
					else {
						g.setColor(colors.unique(a.getClass()));
					}
					g.fillOval(
						horizontalMargin + cellMargin + (location.x * cellSize) + 1,
						verticalMargin   + cellMargin + (location.y * cellSize) + 1,
						cellSize - (cellMargin * 2) - 1,
						cellSize - (cellMargin * 2)
					);
					g.setColor(colors.outline);
					g.drawOval(
						horizontalMargin + cellMargin + (location.x * cellSize) + 1,
						verticalMargin   + cellMargin + (location.y * cellSize) + 1,
						cellSize - (cellMargin * 2) - 1,
						cellSize - (cellMargin * 2)
					);
				}
			}
		}
	}

	private double sofar = 0;
	public void tick(double time) {
		sofar += time;
		if (sofar > .75) {
			controller.next();
			sofar = 0;
		}
	}

	public void flush() {
		
	}

}
