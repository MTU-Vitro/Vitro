package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;

import java.util.*;
import vitro.util.*;

public class SearchHost extends Host {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		// here we process cmd-line arguments to provide the
		// host with the appropriate assignment parameters.

		new SearchHost();
	}
	
	public SearchHost() {
		initializePart1();
	}
	
	public void initializePart1() {
		Maze m = (new MazeFactory(10, 10, 0, 0, new Random(0xDEADFACE))).generate();
		System.out.println(m);
	
		int[][] maze = m.asIntegerGrid();
		maze[21 - 6][21 - 4] = 3;
	
		Robots model = new Robots(maze);
		model.locations.put(model.createBLU(), new Location(model, 1, 1));
		
		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);
		
		controller.bind(Robots.BLU.class, new PathingAgent(PathingAgent.PathType.ASTAR_MANHATTAN));
		
		show(view);
	}
}
