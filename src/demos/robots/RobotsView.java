package demos.robots;

import vitro.*;
import vitro.grid.*;
import java.awt.*;
import java.awt.image.*;
import static vitro.util.Groups.*;

public class RobotsView extends GridView {
	
	private final Robots model;
	private final Image buffer;

	public RobotsView(Robots model, Controller controller) {
		super(model, controller, 720, 720, new ColorScheme());
		this.model = model;
		this.buffer = new BufferedImage(
			model.width * 16,
			model.height * 16,
			BufferedImage.TYPE_INT_RGB
		);

		colors.outline    = Color.WHITE;
		colors.secondary  = Color.GRAY;
		colors.background = Color.BLACK;
		colors.inactive   = Color.GRAY.darker();
	}

	public void draw(Graphics g) {
		Graphics2D bg = (Graphics2D)buffer.getGraphics();
		bg.setColor(colors.background);
		bg.fillRect(0, 0, width, height);

		for(int y = 0; y < model.height; y++) {
			for(int x = 0; x < model.height; x++) {
				drawCell(bg, x, y);
			}
		}
		synchronized(model) {
			for(Actor actor : model.actors) { drawActor(bg, actor); }
		}

		g.drawImage(
			buffer,
			0, 0,           width,                 height,
			0, 0, buffer.getWidth(null), buffer.getHeight(null),
			null
		);
	}

	protected void drawCell(Graphics2D g, int x, int y) {
		if (model.tiles[x][y] == Robots.SOLID) {
			g.setColor(Color.BLUE.darker().darker());
			g.fillRect(
				(x * 16),
				(y * 16),
				16,
				16
			);
		}
		else {
			g.setColor(colors.outline);
			g.drawRect(
				(x * 16),
				(y * 16),
				16,
				16
			);
		}
	}

	protected void drawActor(Graphics2D g, Actor a) {
		super.drawActor(g, a);
	}
}