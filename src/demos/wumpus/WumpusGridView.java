package demos.wumpus;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class WumpusGridView extends GridView {

	public WumpusGridView(Controller controller, int width, int height) {
		super(controller, width, height, new ColorScheme(Color.BLACK, new Color(75, 75, 75), Color.WHITE));
	}

}
