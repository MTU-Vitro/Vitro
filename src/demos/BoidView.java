package demos;

import vitro.*;
import vitro.plane.*;
import java.awt.*;
import java.awt.image.*;

public class BoidView implements View {

	private final int width;
	private final int height;
	private final int horizontalMargin;
	private final int verticalMargin;

	private final double modelScale;

	private final BoidWorld model;
	private final Controller controller;
	private final ColorScheme colors;

	private final Image buffer;
	private final Image target;

	public BoidView(BoidWorld model, Controller controller, int width, int height , ColorScheme colors) {
		this.model      = model;
		this.controller = controller;
		this.width      = width;
		this.height     = height;
		this.colors     = colors;

		modelScale = Math.min(
			width  * .8 / model.width,
			height * .8 / model.height
		);
		horizontalMargin = (int)((width  - (model.width  * modelScale)) / 2);
		verticalMargin   = (int)((height - (model.height * modelScale)) / 2);

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

			g.setColor(colors.outline);
			g.drawRect(
				horizontalMargin,
				verticalMargin,
				(int)(model.width  * modelScale),
				(int)(model.height * modelScale)
			);

			synchronized(model) {
				for(Actor a : model.actors) {
					g.setColor(colors.unique(a.getClass()));
					Position position = model.positions.get(a);
					if(position == null) { continue; }
					g.fillOval(
						(int)(horizontalMargin + (position.x * modelScale) - 4),
						(int)(verticalMargin   + (position.y * modelScale) - 4),
						8, 8
					);
					g.setColor(colors.outline);
					g.drawOval(
						(int)(horizontalMargin + (position.x * modelScale) - 4),
						(int)(verticalMargin   + (position.y * modelScale) - 4),
						8, 8
					);
				}
			}
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
}
