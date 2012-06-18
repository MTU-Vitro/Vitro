package demos.RL;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import static vitro.util.Groups.*;

public class RLView extends GridView {

	public RLView(Controller controller, int width, int height) {
		super(controller, width, height, new ColorScheme());
		colors.setColor(RLActor.class, Color.GRAY);
	}

	protected RLModel model() {
		return (RLModel)controller().model();
	}

	protected void drawCell(Graphics2D g, int x, int y) {
		Rectangle cell = new Rectangle(
			horizontalMargin + (x * cellSize),
			verticalMargin   + (y * cellSize),
			cellSize,
			cellSize
		);

		if (!model().passable(null, new Location(model(), x, y))) {
			g.setColor(colors.inactive);
			g.fill(cell);
		}

		if (model().goal(x, y)) {
			g.setColor(model().reward(x, y) > 0 ?
				Color.GREEN.darker() :
				Color.RED.darker()
			);
			g.fill(cell);

			g.setColor(colors.outline);
			g.drawRect(
				horizontalMargin + (x * cellSize) + 5,
				verticalMargin   + (y * cellSize) + 5,
				cellSize - 10,
				cellSize - 10
			);
		}

		g.setColor(colors.outline);
		g.draw(cell);
	}

	protected void drawVector(Graphics2D g, Location cell, int dir) {
		double py = (cellSize * .50 * -1) + 5;
		double tw = (cellSize * .25);
		double th = (cellSize * .10);

		g.translate(
			horizontalMargin + (cell.x * cellSize) + (cellSize / 2.0),
			verticalMargin   + (cell.y * cellSize) + (cellSize / 2.0)
		);
		g.rotate(Math.PI / 4 * dir);
		
		Path2D.Double shape = new Path2D.Double();
		shape.moveTo(      0, py     );
		shape.lineTo( tw / 2, py + th);
		shape.lineTo(-tw / 2, py + th);
		shape.lineTo(      0, py     );
		g.fill(shape);
	}
}