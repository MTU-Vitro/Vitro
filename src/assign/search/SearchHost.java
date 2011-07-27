package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;

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
	
		int[][] maze = new int[][] {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0 },
			{ 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0 },
			{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
			{ 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
			{ 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 0 },
			{ 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0 },
			{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0 },
			{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 0, 3, 1, 1, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		};
	
		Robots model = new Robots(maze);
		model.locations.put(model.createBLU(), new Location(model, 1, 1));
		
		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);
		
		controller.bind(Robots.BLU.class, new PathingAgent(PathingAgent.PathType.DEPTH));
		
		show(view);
	}
}
