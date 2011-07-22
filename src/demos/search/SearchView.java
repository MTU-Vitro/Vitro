package demos.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class SearchView extends GridView {

	public SearchView(Search model, Controller controller, int width, int height) {
		super(model, controller, width, height, new ColorScheme(Color.WHITE, Color.BLUE, Color.BLACK));
	}
	
	protected void drawCell(Graphics2D g, int x, int y) {
		if(((Search)model).maze[y][x]) { g.setColor(colors.background); }
		else                           { g.setColor(colors.secondary);  }
		g.fillRect(
			horizontalMargin + (x * cellSize),
			verticalMargin   + (y * cellSize),
			cellSize,
			cellSize
		);
		
		g.setColor(colors.outline);
		g.drawRect(
			horizontalMargin + (x * cellSize),
			verticalMargin   + (y * cellSize),
			cellSize,
			cellSize
		);
	}
}
