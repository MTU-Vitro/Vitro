package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;

import java.util.*;
import java.io.*;
import vitro.util.*;

public class SearchHost extends Host {
	private static final long serialVersionUID = 1L;

	/**
	*
	**/
	public static void main(String[] args) {
		// here we process cmd-line arguments to provide the
		// host with the appropriate assignment parameters.
		if(args.length < 1) {
			System.out.println("No Parameters, Starting Default!");
			new SearchHost();
		}
		else {
			new SearchHost(args);
		}
	}

	/**
	*
	**/
	public SearchHost() {
		//this(new String[] { "pathing", "breadth" });
		this(new String[] { "sokoban", "map4" });
	}

	/**
	*
	**/
	public SearchHost(String[] args) {
		if      (args[0].equals("pathing")) { initializePart1(args);                    }
		else if (args[0].equals("sokoban")) { initializePart2(args);                    }
		else                                { System.out.println("Invalid Arguments."); }
	}

	/**
	*
	**/
	public void initializePart1(String[] args) {
		if(args.length < 2) { System.out.println("Invalid Arguments."); return; }

		PathingAgent.PathType type = null;
		if      (args[1].equals("breadth"        )) { type = PathingAgent.PathType.BREADTH;             }
		else if (args[1].equals("depth"          )) { type = PathingAgent.PathType.DEPTH;               }
		else if (args[1].equals("astar-manhattan")) { type = PathingAgent.PathType.ASTAR_MANHATTAN;     }
		else if (args[1].equals("astar-euclidian")) { type = PathingAgent.PathType.ASTAR_EUCLIDIAN;     }
		else                                        { System.out.println("Invalid Algorithm."); return; }

		String filename = "assign/search/path.map";
		int[][] maze = {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0 },
			{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			{ 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0 },
			{ 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 },
			{ 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0 },
			{ 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0 },
			{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0 },
			{ 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 3, 0, 1, 0 },
			{ 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0 },
			{ 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0 },
			{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 0 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
		};

		Robots     model      = new Robots(maze);
		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);

		model.locations.put(model.createBLU(), new Location(model, 1, 1));
		controller.bind(Robots.BLU.class, new PathingAgent(type));
		
		show(view);
	}

	/**
	*
	**/
	public void initializePart2(String[] args) {
		if(args.length < 2) { System.out.println("Invalid Arguments."); return; }

		Robots model;
		if(args[1].equals("map0")) {
			int[][] maze = {
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 3, 1, 1, 1, 1, 3, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }
			};

			model = new Robots(maze);
			model.locations.put(model.createBLU()  , new Location(model, 1, 1));
			model.locations.put(model.createBlock(), new Location(model, 2, 1));
		}
		else if(args[1].equals("map1")) {
			int[][] maze = {
				{ 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 1, 1, 1, 3, 3, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0 }
			};

			model = new Robots(maze);
			model.locations.put(model.createBLU()  , new Location(model, 1, 1));
			model.locations.put(model.createBlock(), new Location(model, 2, 1));
		}
		else if(args[1].equals("map2")) {
			String filename = "assign/search/sokoban.blu2.map";
			int[][] maze = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
			};

			model = new Robots(maze);
			model.locations.put(model.createBLU()  , new Location(model, 1, 1));
			model.locations.put(model.createBlock(), new Location(model, 2, 2));
		}
		else if(args[1].equals("map3")) {
			int[][] maze = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
			};

			model = new Robots(maze);
			model.locations.put(model.createBLU()  , new Location(model, 1, 1));
			model.locations.put(model.createBlock(), new Location(model, 2, 2));
		}
		else if(args[1].equals("map4")) {
			int[][] maze = {
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 0 },
				{ 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
			};

			model = new Robots(maze);
			model.locations.put(model.createBLU()  , new Location(model, 1, 1));
			model.locations.put(model.createBlock(), new Location(model, 2, 2));
		}
		else { System.out.println("Invalid Map."); return; }

		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);

		controller.bind(Robots.BLU.class, new SokobanAgentBLU());

		show(view);
	}
}
