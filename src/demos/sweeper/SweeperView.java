package demos.sweeper;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class SweeperView extends GridView {
	public final Sweeper model;

	public SweeperView(Sweeper model, Controller controller, int width, int height) {
		super(model, controller, width, height, new ColorScheme());
		this.model = model;
	}
	
	protected void drawCell(Graphics2D g, int x, int y) {
		synchronized(model) {
			//System.out.println(((Mines)model).count(new Location(model, 0, 0)));
			if(model.hidden.contains(new Location(model, x, y))) {
				g.setColor(colors.secondary);
				g.fillRect(
					horizontalMargin + (x * cellSize),
					verticalMargin   + (y * cellSize),
					cellSize,
					cellSize
				);
			}
			else {
				Location location = new Location(model, x, y);
				if(!Groups.containsType(Sweeper.Mine.class, model.actorsAt(location))) {
					g.setColor(colors.outline);
					Drawing.drawStringCentered(
						g,
						""+model.count(location),
						horizontalMargin + cellSize/2 + (location.x * cellSize),
						verticalMargin   + cellSize/2 + (location.y * cellSize)
					);
				}
			}
		}

		super.drawCell(g, x, y);
	}

	protected void drawActor(Graphics2D g, Actor a) {
		if(a instanceof Sweeper.Mine && model.hidden.contains(model.locations.get(a))) {
			return;
		}
		super.drawActor(g, a);
	}
}
