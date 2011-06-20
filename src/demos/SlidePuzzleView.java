package demos;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.image.*;

public class SlidePuzzleView implements View {

	private final int width;
	private final int height;
	private final int cellSize;
	private final int horizontalMargin;
	private final int verticalMargin;

	private final SlidePuzzle model;
	private final Controller controller;
	private final ColorScheme colors;

	private final Image buffer;
	private final Image target;

	public SlidePuzzleView(SlidePuzzle model, Controller controller, int width, int height, ColorScheme colors) {
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
			g.setFont(g.getFont().deriveFont(30.0f));

			for(int y = 0; y < model.height; y++) {
				for(int x = 0; x < model.height; x++) {
					g.setColor(colors.outline);
					g.drawRect(
						horizontalMargin + (x * cellSize),
						verticalMargin   + (y * cellSize),
						cellSize,
						cellSize
					);
					Drawing.drawStringCentered(
						g,
						(model.numbers[y][x] == 0) ? "" : String.format("%d", model.numbers[y][x]),
						horizontalMargin + (x * cellSize) + (cellSize / 2),
						verticalMargin   + (y * cellSize) + (cellSize / 2)
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
					Location location = model.locations.get(a);
					if (location == null) { continue; }
					g.setColor(colors.unique(a));
					g.fillRect(
						horizontalMargin + (location.x * cellSize) + 1,
						verticalMargin   + (location.y * cellSize) + 1,
						cellSize - 1,
						cellSize
					);
					g.setColor(colors.unique(a).darker());
					g.fillRect(
						horizontalMargin + (location.x * cellSize) + 1,
						verticalMargin   + (location.y * cellSize) + 1,
						cellSize - 1,
						(cellSize / 10) - 1 
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
