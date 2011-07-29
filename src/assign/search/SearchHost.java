package assign.search;

import vitro.*;
import vitro.grid.*;
import demos.robots.*;

import java.util.*;
import java.io.*;
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
		//initializePart2();
	}
	
	public void initializePart1() {
		Maze m = (new MazeFactory(10, 20, 0, 0, new Random(0xDEADFACE))).generate();
		System.out.println(m);
	
		int[][] maze = m.asIntegerGrid();
		maze[21 - 6][21 - 4] = 3;
		
		//maze = loadRoom("search.map");

		Robots model = new Robots(maze);
		model.locations.put(model.createBLU(), new Location(model, 1, 1));
		
		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);
		
		controller.bind(Robots.BLU.class, new PathingAgent(PathingAgent.PathType.ASTAR_MANHATTAN));
		
		show(view);
	}
	
	public void initializePart2() {
		String filename = "assign/search/sokoban.blu0.map";
		int[][] maze = loadRoom(SearchHost.class.getClassLoader().getResource(filename).getFile());
		
		Robots model = new Robots(maze);
		model.locations.put(model.createBLU(), new Location(model, 2, 2));
		
		Controller controller = new SequentialController(model);
		RobotsView view       = new RobotsView(model, controller);
		
		controller.bind(Robots.BLU.class, new PathingAgent(PathingAgent.PathType.ASTAR_MANHATTAN));
		
		show(view);
	}
	
	private int[][] loadRoom(String filename) {
		List<List<Integer>> data = new ArrayList<List<Integer>>();
		try {
			Scanner scanner = new Scanner(new File(filename));
	
			while(scanner.hasNextLine()) {
				Scanner lineScanner = new Scanner(scanner.nextLine());
				
				List<Integer> line = new ArrayList<Integer>();
				while(lineScanner.hasNextInt()) {
					line.add(lineScanner.nextInt());
				}
				data.add(line);
			}
		}
		catch(Exception e) {
			System.out.println(e);
			return new int[][] {
				{ 0, 0, 0 },
				{ 0, 3, 0 },
				{ 0, 0, 0 }
			};
		}
		
		int[][] grid = new int[data.size()][data.get(0).size()];
		for(int y = 0; y < data.size(); y++) {
			for(int x = 0; x < data.get(0).size(); x++) {
				grid[y][x] = data.get(y).get(x);
				System.out.print(grid[y][x]);
			}
			System.out.println();
		}
		
		return grid;
	}
}
